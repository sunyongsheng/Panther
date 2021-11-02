async function upload(appKey, dir, file) {
    const config = {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }
    const formData = new FormData();
    formData.append("app_key", appKey);
    formData.append("file", file);
    if (dir) {
        formData.append("dir", dir);
    }
    return axiosClient.post("/api/v1/admin/image", formData, config);
}

function formatDate(date, fmt) {
    const o = {
        "M+": date.getMonth() + 1,                 //月份
        "d+": date.getDate(),                    //日
        "h+": date.getHours(),                   //小时
        "m+": date.getMinutes(),                 //分
        "s+": date.getSeconds(),                 //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    let k;
    for (k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}

function getSizeFromByte(byte) {
    const KB = 1024;
    const MB = 1048576;
    if (byte < KB) {
        return byte + 'B'
    } else if (byte < MB) {
        const k = byte / 1024
        return k.toFixed(2) + 'K'
    } else {
        const m = byte / MB;
        return m.toFixed(2) + 'M';
    }
}

function ellipsisText(text, len) {
    if (typeof text === 'string') {
        if (text.length < len) return text;
        return text.substring(0, len - 3) + '...';
    }
    return text;
}

function findFirstIndex(list, condition) {
    let i = 0;
    for (i; i < list.length; i++) {
        let element = list[i];
        if (condition(element)) {
            return i;
        }
    }
    return -1;
}