<template>
  <div>
    <div class="item">用户名：{{admin.username}}</div>
    <div class="item">用户的联系方式：{{admin.phone}}</div>
    <div class="item">用户的邮箱：{{admin.email}}</div>
  </div>
</template>

<script>
// @ is an alias to /src
  import Cookies from "js-cookie";
  import request from "@/utils/request";
  export default {
    data() {
      return {
        admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {}
      }
    },
    created() {
      // request.get('/admin/' + this.admin.id).then(res =>{
      //   this.admin = res.data
      // })
    }
  }
</script>
<style>
.item {
  margin: 10px 0;
  color: #666;
  margin-left: 10px;
}
</style>
