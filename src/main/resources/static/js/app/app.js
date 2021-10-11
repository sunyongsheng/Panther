const allNamingStrategySample = {
    UUID: '2138dui2sdf0kfn.jpg',
    ORIGIN: 'test.jpg',
    DATE_UUID_HYPHEN: '2021-12-31-2138dui2sdf0kfn.jpg',
    DATE_ORIGIN_HYPHEN: '2021-12-31-test.jpg',
    DATE_UUID_UNDERLINE: '2021_12_31_2138dui2sdf0kfn.jpg',
    DATE_ORIGIN_UNDERLINE: '2021_12_31_test.jpg'
};

async function getAllAppData(page, pageSize) {
    return axiosClient.get(`/api/v1/apps?page=${page}&page_size=${pageSize}`)
}

async function generateUploadToken(appKey) {
    return axiosClient.post(`api/v1/app/uploadToken?app_key=${appKey}`)
}

async function updateAppInfo(appKey, param) {
    return axiosClient.put(`/api/v1/app/?app_key=${appKey}`, param)
}

async function lockApp(appKey) {
    return axiosClient.post(`/api/v1/app/lock/?app_key=${appKey}`)
}

async function unlockApp(appKey) {
    return axiosClient.post(`/api/v1/app/unlock/?app_key=${appKey}`)
}

async function deleteApp(appKey) {
    return axiosClient.post(`/api/v1/app/delete/?app_key=${appKey}`)
}

async function undeleteApp(appKey) {
    return axiosClient.post(`/api/v1/app/undelete/?app_key=${appKey}`)
}