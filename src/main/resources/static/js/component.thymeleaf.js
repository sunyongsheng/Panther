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
        console.log("logout")
    }
}
