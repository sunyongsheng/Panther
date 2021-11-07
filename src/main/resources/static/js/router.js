function onSelectSideMenu(index, _) {
    window.location.href = "/admin/" + index;
}

function onClickPantherSetting() {
    window.location.href = "/admin/setting";
}

function onClickUsernameMenu(command) {
    if (command === 'changePassword') {
        window.location.href = "/admin/changePassword";
    } else if (command === 'logout') {
        logout()
    }
}

function logout() {
    let keys = document.cookie.match(/[^ =;]+(?==)/g)
    if (keys) {
        for (let i = keys.length; i--;) {
            document.cookie = keys[i] + '=0;path=/;expires=' + new Date(0).toUTCString();
        }
    }
    window.location.href = "/login";
}
