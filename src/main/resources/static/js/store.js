const currUser = {
    username: '',
    email: '',
    avatar: '',
    initSuccess: false
}

function initializeAdminInfo() {
    if (!currUser.initSuccess) {
        getAdminInfo().then(response => {
            let info = response.data;
            currUser.username = info.username;
            currUser.email = info.email;
            currUser.avatar = gravatarUrl(info.email);
            currUser.initSuccess = true;
        }).catch(e => {
            currUser.username = '获取失败';
            currUser.initSuccess = false;
            console.error(e);
        });
    }
}

function gravatarUrl(email) {
    let hash = md5(email.toLowerCase())
    return `https://www.gravatar.com/avatar/${hash}`
}