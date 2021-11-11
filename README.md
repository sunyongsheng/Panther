# Panther

## 安装

一、创建Panther数据库：`CREATE DATABASE panther CHARACTER SET utf8mb4;`

二、二选一：

a. 修改 `application.properties` 中的数据库用户名和密码**以及**根目录下的`build.gradle.kts`中的`flyway`的用户名及密码；

b. MySQL中运行：
```mysql
CREATE USER 'pantherAdmin'@'%' IDENTIFIED by 'pantherJF=A77922';
GRANT ALL ON panther.* TO 'pantherAdmin'@'%';
```

三、访问 `http://localhost:8088/install` 进行安装，安装后手动重启；


## 概念

1. 存储根路径：指Panther运行时所管理目录的绝对路径，上传的所有图片都将存储在此路径下；整个流程中所涉及的文件夹也都在此路径下；
2. App：所有**正常路径**上传的图片必须归属于某个App，除非其原本就存在于存储根路径下；创建App时，将使用App的英文名作为文件夹名在`app`目录下创建文件夹；
3. App专属空间/App目录：`app`目录下，以App英文名作为名称的文件夹。App目录只对其App可见，任何App无法上传到不属于自己的文件夹中；
4. 超级App：相较于其他App，可以自定义上传目录的App，但无法上传到其他App目录；

## 流程
| Description | Method | API | Header | Param | Body | Return |
| --- | --- | --- | --- | --- | --- | --- |
| 管理员登录，获取管理员Token | GET | `/adminLogin` | - | username;password | - | token |
| 创建App，获取AppKey | POST | `/api/v1/app` | Authorization=$token | - | CreateAppParam | appKey |
| 生成App上传Token | POST | `/api/v1/app/uploadToken` | Authorization=$token | app_key=$appKey | - | uploadToken |
| 上传图片 | POST | `/api/v1/image` | Upload-Token=$uploadToken | file | - | ImageDTO |