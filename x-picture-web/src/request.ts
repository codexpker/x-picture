import axios from 'axios'
import { message } from 'ant-design-vue'

const myAxios = axios.create({
  baseURL: 'http://localhost:8436/api',
  timeout: 120000,
  //是否携带Cookie
  withCredentials: true,
});


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
  // 2xx 范围内的状态码都会触发该函数。
  const { data } = response;
  // 未登录
  if(data.code === 40100){
    // 如果不是获取用户信息的请求（即使获取到未登录也不用跳转——不是什么情况都需要登录查看信息），并且用户不是已经在登录页面
    if(!response.request.responseURL.includes('/user/get/login') &&
       !window.location.pathname.includes('/user/login')
    ){
      message.warning("请先登录")
      window.location.href = `/user/login?redirect=${window.location.href}`
    }
  }
  // 对响应数据做点什么
  return response;
}, function (error) {
  // 超出 2xx 范围的状态码都会触发该函数。
  // 对响应错误做点什么
  return Promise.reject(error);
});

export default myAxios;
