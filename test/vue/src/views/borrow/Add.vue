<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px;margin-left: 30px">新增借书记录</h2>
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
        <el-input v-model="form.bookName" disabled></el-input>
      </el-form-item>

      <el-form-item label="图书所需积分" prop="score">
        <el-input v-model="form.score" disabled></el-input>
      </el-form-item>

      <el-form-item label="图书数量" prop="nums">
        <el-input v-model="form.nums" disabled></el-input>
      </el-form-item>

      <el-form-item label="会员码" prop="userNo">
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
  name: "AddBorrow",
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
  // 加载之后去请求这个book列表，给books赋值
  created() {
    request.get('/book/list').then(res => {
      this.books = res.data
    })

    request.get('/user/list').then(res => {
      this.users = res.data
    })
  },
  methods: {
    selUser() {
      const user = this.users.find(v => v.username === this.form.userNo)
      this.form.userName = user.name
      this.form.userPhone = user.phone
    },
    selBookName() {
      const book = this.books.find(v => v.bookNo === this.form.bookNo)
      this.form.bookName = book.name
      this.form.score = book.score
      this.form.nums = book.nums
    },
    save() {
      this.$refs["ruleForm"].validate((valid) => {
        if (valid){
          request.post('/borrow/save',this.form).then(res => {
            if (res.code === '200') {
              this.$notify.success('新增成功')
              this.$refs['ruleForm'].resetFields()
            } else {
              this.$notify.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>
<style scoped>

</style>