<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px;margin-left: 30px">编辑图书信息</h2>
    <el-form :inline="true" :model="form" :rules="rules" ref="ruleForm" label-width="100px">

      <el-form-item label="图书标准码" prop="bookNo">
        <el-select v-model="form.bookNo" clearable filterable placeholder="请选择" @change="selBookName">
          <el-option
              v-for="item in books"
              :key="item.id"
              :label="item.bookNo"
              :value="item.bookNo">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="图书名称" prop="bookName">
        <el-input v-model="form.bookName" disabled placeholder="请输入名称"></el-input>
      </el-form-item>

      <el-form-item label="图书所需积分" prop="score">
        <el-input v-model="form.score" disabled></el-input>
      </el-form-item>

      <el-form-item label="用户码" prop="userNo">
        <el-select v-model="form.userNo" filterable placeholder="请选择" @change="selUser">
          <el-option
              v-for="item in users"
              :key="item.id"
              :label="item.username"
              :value="item.username">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="用户名" prop="userName">
        <el-input v-model="form.userName" disabled></el-input>
      </el-form-item>
      <el-form-item label="用户联系方式" prop="userPhone">
        <el-input v-model="form.userPhone" disabled></el-input>
      </el-form-item>

    </el-form>
    <div style="text-align: center;margin-top: 30px">
      <el-button type="primary" @click="save" size="medium">提交</el-button>
<!--      <el-button type="danger" >取消</el-button>-->
    </div>
  </div>
</template>
<script>
import request from "@/utils/request"
export default {
  name: "EditBorrow",
  data() {
    return {
      form: {},
      books: [],
      users: [],
      categories: [],
      rules: {
        bookNo: [
          {required: true, message: '请输入图书标准码', trigger: 'blur'},
        ],
        userNo: [
          {required: true, message: '请输入会员码', trigger: 'blur'},
        ]
      }
    }
  },
  created() {
    request.get('/book/list').then(res => {
      this.books = res.data
    })

    request.get('/user/list').then(res => {
      this.users = res.data
    })

    const id = this.$router.query.id
    request.get('/borrow/' + id).then(res => {
      this.form = res.data
    })
  },
  methods: {
    save() {
      request.put('/borrow/update',this.form).then(res => {
        if (res.code === '200') {
          this.$notify.success('更新成功')
          this.$router.push("/borrowList")
        }else {
          this.$notify.error(res.msg)

        }
      })
    }
  }
}
</script>
<style scoped>

</style>