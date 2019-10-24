<%--
  Created by IntelliJ IDEA.
  User: 臧其潇
  Date: 2019/10/9
  Time: 13:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<html>
<head>
    <style>
        body{
            padding: 0px;
            margin: 0px;
        }
        .avatar-uploader .el-upload {
            border: 1px dashed #d9d9d9;
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
        }
        .avatar-uploader .el-upload:hover {
            border-color: #409EFF;
        }
        .avatar-uploader-icon {
            font-size: 28px;
            color: #8c939d;
            width: 100px;
            height: 100px;
            line-height: 100px;
            text-align: center;
        }
        .avatar {
            width: 100px;
            height: 100px;
            display: block;
        }
    </style>
    <title>角色列表</title>
    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
</head>
<body>
<div id="app">
    <el-dialog title="添加/修改信息" :visible.sync="dialogFormVisible" width="430px">
        <el-form :model="role" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="role.roleName"></el-input>
            </el-form-item>


        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="quxiaotijiao">取 消</el-button>
            <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
        </div>
    </el-dialog>

    <el-dialog title="授权" :visible.sync="dialogPowerVisible" width="430px">
        <el-tree :data="data" ref="tree" :props="defaultProps"
                 @check="changepower"
                 :check-on-click-node="false"
                 show-checkbox
                 :highlight-current="true"
                 :default-expand-all="true"
                 node-key="menuId"
                 :default-checked-keys="ckarray"

        >
        </el-tree>

    </el-dialog>




    <el-row :gutter="20" style="margin-bottom: 10px">
        <el-col :span="5"><el-button-group>
            <el-button type="primary" icon="el-icon-edit"></el-button>
            <el-button type="primary" icon="el-icon-circle-plus-outline" @click="add"></el-button>
        </el-button-group>
        </el-col>

    </el-row>

    <template>
        <el-table
                :data="list"
                ref="tb1"
                border
                style="width: 100%"
                height="450">
            <el-table-column
                    type="selection"
                    width="55">
            </el-table-column>
            <el-table-column
                    fixed
                    prop="roleId"

                    label="ID"
                    width="60">
            </el-table-column>

            <el-table-column
                    prop="roleName"
                    label="角色姓名"
                    width="120">

            </el-table-column>

            <el-table-column
                    prop="roleCreatetime"
                    label="创建时间"

                    width="250">
            </el-table-column>
            <el-table-column
                    prop="roleLastmodify"
                    label="最后修改时间"

                    width="250">
            </el-table-column>
            <el-table-column
                    fixed="right"
                    label="操作"
                   >
                <template slot-scope="scope">
                    <el-button
                            @click="power(scope.row.roleId)"
                            type="text"
                            >
                        授权
                    </el-button>
                    <el-button
                            @click="update(scope.row)"
                            type="text"
                            size="small">
                        编辑
                    </el-button>
                    <el-button
                            @click.native.prevent="deletex(scope.row.roleId)"
                            type="text"
                            size="small">
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
    </template>


</div>
<script src="https://unpkg.com/vue/dist/vue.js"></script>
<!-- import JavaScript -->
<script src="https://unpkg.com/element-ui/lib/index.js"></script>
<script src="https://cdn.jsdelivr.net/npm/axios@0.12.0/dist/axios.min.js"></script>
<script src="https://cdn.bootcss.com/qs/6.5.1/qs.min.js"></script>
<script>
    new Vue({
        el:'#app',
        data:function(){
            return {
                //默认权限选中
                ckarray:[],
                defaultProps: {
                    children: 'list',
                    label: 'menuTitle',
                },
                data:[],
                list: [],
                dialogFormVisible: false,
                dialogPowerVisible:false,
                 role: {},
                rules: {
                    roleName: [
                        {required: true, message: '请输入角色姓名', trigger: 'blur'},
                        {min: 2, max: 5, message: '长度在 2 到 5 个字符', trigger: 'blur'}
                    ],
                },
                currentid:0
            }
        },
        created:function(){
              this.getdata();
        },
        methods:{
            changepower(){
                this.savepower();

            },
            savepower(){
                //当前树选中的节点
                var nodes=this.$refs.tree.getCheckedNodes(false,false);
                var ids=[];
                for(var i=0;i<nodes.length;i++){
                    ids.push(nodes[i].menuId);
                }
                var data={
                    roleid:this.currentid,
                    menuids:ids.join(",")
                }
                var self=this;
                axios.get("<%=basePath%>admin/role/power",{
                        params:data
                    }
                ).then(function(response){
                    if(response.data.code=='10000'){
                        self.$message({
                            type: 'success',
                            message: '保存成功!'
                        });
                    }else{
                        self.$message({
                            type: 'error',
                            message: '保存失败!'
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                    self.$message({
                        type: 'error',
                        message: '网络异常!'
                    });
                });

            },
            power(id){
                this.currentid=id;
                var self=this;
                axios.get("<%=basePath%>admin/role/powerlist",{
                        params:{
                            roleid: id
                        }
                    }
                ).then(function(response){
                   self.ckarray=[];
                    if(response.data.code=='10000'){
                       var list=response.data.obj;
                   //    console.log("输出1"+list[1].rmrfMenuid);
                       for(var i=0;i<list.length;i++){
                           self.ckarray.push(list[i].rmrfMenuid);
                           console.log("输出2"+self.ckarray[i].rmrfMenuid)
                       }
                        self.inittree();
                        self.dialogPowerVisible=true;
                    }else{
                        self.$message({
                            type: 'error',
                            message: '权限加载失败!'
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                    self.$message({
                        type: 'error',
                        message: '网络异常!'
                    });
                });

            },
            handleNodeClick(data,node,dom) {
                  this.savepower()


        },
            inittree(){
                var self=this;
                axios.get("<%=basePath%>admin/menu/list").then(function(response){
                    if(response.data.code=='10000'){
                        self.data=response.data.obj;
                    }else{
                        self.$message({
                            type: 'error',
                            message: '菜单加载失败!'
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                    self.$message({
                        type: 'error',
                        message: '网络异常!'
                    });
                });

            },
            add(){
              this.dialogFormVisible=true;
              this.role={}
            },
            // images(){
            //   if(this.manager.managerImg!=null){
            //       return this.manager.managerImg;
            //   }  else{
            //       return this.imageUrl;
            //   }
            // },
            quxiaotijiao(){
               this.dialogFormVisible = false;
                this.getdata()
            },

            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                     this.save();
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },

            update(row){
                this.role=row;
                this.dialogFormVisible=true;
            },

            save(){
                var self=this;
                axios.post('<%=basePath%>admin/role/saveOrupdate',Qs.stringify(self.role))
                    .then(function(res){
                         if(res.data.code=='10000'){
                             self.$message({
                                 type: 'success',
                                 message: '保存成功!'
                             });
                             //重新加载数据
                             self.getdata();
                             //隐藏
                             self.dialogFormVisible=false;
                         }else {
                             self.$message({
                                 type: 'error',
                                 message: '保存失败!'
                             });
                         }
                    }).catch(()=>{
                    self.$message({
                        type: 'error',
                        message: '网络异常!'
                    });
                })
            },

            deletex(id){
                var self=this;
                this.$confirm('是否删除本行数据', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    var data={
                        id:id
                    };
                    axios.get("<%=basePath%>admin/role/delete",{
                        params:data
                    }).then(function(response){
                        if(response.data.code=='10000'){
                            self.$message({
                                type: 'success',
                                message: '删除成功!'
                            });
                           self.getdata();
                        }else{
                            self.$message({
                                type: 'error',
                                message: '删除失败!'
                            });
                        }
                    }).catch(function(error){
                        console.log(error);
                      self.$message({
                            type: 'error',
                            message: '网络异常!'
                        });
                    });

                }).catch(() => {

                });

            },

             getdata:function(){
                 var self=this;
                 axios.get("<%=basePath%>admin/role/list").then(function(response){
                    if(response.data.code=='10000'){
                        self.list=response.data.obj;
                    }
                 }).catch(function(error){
                     console.log(error);
                 });
             }
        }

    });
</script>
</body>
</html>
