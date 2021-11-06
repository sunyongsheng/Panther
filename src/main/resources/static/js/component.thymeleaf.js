function onSelectSideMenu(index, _) {
    window.location.href = "/admin/" + index;
}

function onClickPantherSetting() {
    console.log("setting")
}

function onClickUsernameMenu(command) {
    if (command === 'changePassword') {
        console.log("changePassword")
    } else if (command === 'logout') {
        console.log("logout")
    }
}
