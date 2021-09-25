# Panther

## 安装

一、创建Panther数据库：`CREATE DATABASE panther CHARACTER SET utf8mb4;`

二、运行 `use panther;`，并分别创建 `src/main/resources/sql` 文件夹下的数据库表；

三、二选一：

a. 修改 `application.properties` 中的数据库用户名和密码；

b. MySQL中运行：
```mysql
CREATE USER 'pantherAdmin'@localhost IDENTIFIED by 'pantherJF=A77922';
GRANT ALL ON panther.* TO 'pantherAdmin'@localhost;
```

四、访问 `http://localhost:8088/admin` 进行安装，安装后手动重启；

## 流程

1. 管理员登录，获取管理员Token -> `/adminLogin`；
2. 使用管理员Token: 创建App，获取AppKey -> `/api/v1/app`；
3. 使用管理员Token与AppKey: 为App生成上传Token -> `/api/v1/app/uploadToken`；
4. 使用上传Token: 上传图片 -> `/api/v1/image`