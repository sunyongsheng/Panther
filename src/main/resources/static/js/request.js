axios.defaults.baseURL = 'http://127.0.0.1:8088';
axios.defaults.headers['Content-Type'] = 'application/json';

// 添加请求拦截器
axios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use(function (response) {
    if (response.data) {
        let pantherResponse = response.data;
        if (pantherResponse.code !== 200) {
            console.error(pantherResponse)
            return Promise.reject(pantherResponse);
        }
        return pantherResponse;
    }
    console.error(response)
    return Promise.reject(response);
}, function (error) {
    console.error(error)
    return Promise.reject(error);
});

let axiosClient = axios