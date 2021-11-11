async function getRecent7daysSummary() {
    return axiosClient.get('/api/v1/admin/overview/recent7day');
}

async function getTop7uploadApp() {
    return axiosClient.get('/api/v1/admin/overview/top7upload');
}

async function changePassword(changePasswordParam) {
    return axiosClient.put(`/api/v1/admin/password`, changePasswordParam);
}

async function getPantherConfig() {
    return axiosClient.get(`/api/v1/admin/config`);
}

async function updateHostUrl(adminEmail, hostUrl) {
    return axiosClient.put(`/api/v1/admin/config?changed=hostUrl`, {
        adminEmail: adminEmail,
        urlOrPath: hostUrl
    });
}

async function updateSaveRootPath(adminEmail, saveRootPath) {
    return axiosClient.put(`/api/v1/admin/config?changed=saveRootPath`, {
        adminEmail: adminEmail,
        urlOrPath: saveRootPath
    });
}