package top.aengus.panther.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.access.HttpOrHttpsAccess;
import top.aengus.panther.core.GlobalConfig;
import top.aengus.panther.core.Response;
import top.aengus.panther.tool.FileUtil;
import top.aengus.panther.tool.IPUtil;
import top.aengus.panther.tool.DoubleKeys;
import top.aengus.limiter.main.SimpleCurrentLimiter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private static final String URL_SEPARATOR = "/";

    public static SimpleCurrentLimiter uploadLimiter = new SimpleCurrentLimiter(1, 1);
    public static SimpleCurrentLimiter cloneLimiter = new SimpleCurrentLimiter(3, 1);

    @RequestMapping("/upload")
    @ResponseBody
    public Response<String> upload(@RequestParam MultipartFile file, @RequestParam String dir,
                                   HttpServletRequest request, HttpSession session) {
        synchronized (this) {
            String addr = IPUtil.getIpAddr(request).replaceAll("\\.", "/").replaceAll(":", "/");
            boolean allowed = uploadLimiter.access(addr);
            Response<String> response = new Response<>();
            if (GlobalConfig.adminOnly()) {
                logger.debug("AdminOnly mode is on! Checking user's permission...");
                if (!logged(session)) {
                    logger.error("User not logged! Uploading terminated.");
                    response.setCode(401);
                    response.setMsg("?????????????????????????????????????????????");
                    return response;
                }
                logger.info("Admin is uploading...");
            }
            try {
                while (!allowed) {
                    allowed = uploadLimiter.access(addr);
                    System.out.print(".");
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {}
            //?????????????????????
            if (file.isEmpty()) {
                response.setCode(406);
                return response;
            }
            //?????????????????????
            String originalFilename = file.getOriginalFilename();
            if (FileUtil.isPic(originalFilename)) {
                File dest = FileUtil.generateFile(dir, originalFilename, false);
                response.setData(originalFilename);
                String saveName = dest.getName();
                logger.debug("Saving into {}", dest.getAbsolutePath());
                FileUtil.checkAndCreateDir(dest.getParentFile());
                try {
                    file.transferTo(dest);
                    response.setCode(200);
                    response.setMsg(getCorrectDirPath(dir) + saveName);
                    GlobalConfig.imageUploadedCount(GlobalConfig.imageUploadedCount() + 1);
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                response.setCode(500);
                response.setMsg("??????jpg/jpeg/png/svg/gif?????????");
                return response;
            }
            response.setCode(500);
            response.setMsg("???????????????");
            return response;
        }
    }

    // TODO ??????imageService????????????
    @RequestMapping("/clone")
    @ResponseBody
    public Response<String> clone(String url, String dir, HttpServletRequest request, HttpSession session) {
        synchronized (this) {
            String addr = IPUtil.getIpAddr(request).replaceAll("\\.", "/").replaceAll(":", "/");
            // IP????????????????????????
            boolean allowed = cloneLimiter.access(addr);
            try {
                while (!allowed) {
                    allowed = cloneLimiter.access(addr);
                    System.out.print(".");
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {}
            Response<String> response = new Response<>();
            // ??????IP?????????????????????????????????
            if (!DoubleKeys.check(addr, url)) {
                response.setCode(401);
                response.setMsg("???????????????????????????????????????????????????????????????\"??????\"????????????????????????????????????");
                return response;
            }
            if (GlobalConfig.adminOnly()) {
                logger.debug("AdminOnly mode is on! Checking user's permission...");
                if (!logged(session)) {
                    logger.error("User not logged! Uploading terminated.");
                    response.setCode(401);
                    response.setMsg("?????????????????????????????????????????????");
                    return response;
                }
                logger.debug("Admin is uploading...");
            }
            String regex = "(http(s)?://)?(localhost|(127|192|172|10)\\.).*";
            Matcher matcher = Pattern.compile(regex).matcher(url);
            if (matcher.matches()) {
                response.setCode(401);
                response.setMsg("Anti-SSRF??????????????????????????????????????????????????????");
                return response;
            }
            File dest = null;
            try {
                String suffixName = FileUtil.getExtension(url);
                logger.info("SuffixName: " + suffixName);
                if (FileUtil.isPic(suffixName)) {
                    dest = FileUtil.generateFile(suffixName, dir, true);
                } else {
                    dest = FileUtil.generateFile(".png", dir, true);
                }
                logger.debug("Saving into " + dest.getAbsolutePath());
                FileUtil.checkAndCreateDir(dest.getParentFile());
                FileOutputStream fileOutputStream = new FileOutputStream(dest);
                BufferedInputStream bufferedInputStream = HttpOrHttpsAccess.post(url,
                        "",
                        null);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = bufferedInputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                bufferedInputStream.close();
                Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(url);
                if (m.find()) {
                    response.setData("From " + m.group());
                    response.setCode(200);
                    response.setMsg(getCorrectDirPath(dir) + dest.getName());
                    GlobalConfig.imageUploadedCount(GlobalConfig.imageUploadedCount() + 1);
                } else {
                    response.setData("??????????????????");
                    response.setCode(400);
                }
                return response;
            } catch (Exception e) {
                // ??????????????????????????????????????????????????????????????????
                if (dest != null) {
                    logger.debug("An exception has caught, deleting picture cache...");
                    dest.delete();
                }
                response.setCode(500);
                response.setMsg(e.getClass().toGenericString().replaceAll("public class ", ""));
                return response;
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    public boolean logged(HttpSession session) {
        try {
            return Boolean.parseBoolean(session.getAttribute("admin").toString());
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    /**
     *
     * @return ???????????? /dir/
     */
    String getCorrectDirPath(String dir) {
        if (GlobalConfig.customSavePath) {
            if (dir == null || dir.isEmpty()) {
                return GlobalConfig.defaultSaveDir() + URL_SEPARATOR;
            }
            return URL_SEPARATOR + dir + URL_SEPARATOR;
        }
        return "/uploadImages/";
    }
}