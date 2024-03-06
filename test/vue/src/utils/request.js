import axios from 'axios';
import router from "@/router";
import Cookies from "js-cookie";

const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 50000
})

request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    const adminJSON = Cookies.get('admin')
    if (adminJSON) {
        // 设置请求头
        config.headers['token'] = JSON.parse(adminJSON).token
    }
    return config
},error => {
    return Promise.reject(error)
});

request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res === 'string'){
            res = res ? JSON.parse(res) : res
        }
        if(res.code === '401') {
            router.push('/login')
        }
        return res;
    },
    error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)

export default request