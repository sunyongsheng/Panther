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

## 流程
| Description | Method | API | Header | Param | Body | Return |
| --- | --- | --- | --- | --- | --- | --- |
| 管理员登录，获取管理员Token | GET | `/adminLogin` | - | username;password | - | token |
| 创建App，获取AppKey | POST | `/api/v1/app` | Authorization=$token | - | CreateAppParam | appKey |
| 生成App上传Token | POST | `/api/v1/app/uploadToken` | Authorization=$token | app_key=$appKey | - | uploadToken |
| 上传图片 | POST | `/api/v1/image` | Upload-Token=$uploadToken | file | - | ImageDTO |