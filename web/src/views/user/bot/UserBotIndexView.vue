<template>
  <div class="container">
    <div class="row">
        <div class="col-3">
            <div class="card" style="margin-top: 20px">
                <div class="card-body">
                    <img :src="$store.state.user.photo" alt="" style="width: 100%">
                </div>
            </div>
        </div>
        <div class="col-9">
            <div class="card" style="margin-top: 20px;">
                <div class="card-header">
                    <span style="font-size: 120%">我的机器人</span>
                    <button type="button" class="btn btn-primary float-end btn_style" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                        添加机器人
                    </button>

                    <!-- Modal -->
                    <div class="modal fade modal-xl" id="add-bot-btn">
                    <div class="modal-dialog">
                        <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5">添加机器人</h1>
                            <button type="button" class="btn-close " data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                           <div class="mb-3">
                                <label for="add-bot-title" class="form-label">名称</label>
                                <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入名称">
                            </div>
                            <div class="mb-3">
                                <label for="add-bot-description" class="form-label">简介</label>
                                <br/>
                                  <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入简介"></textarea>
                            </div>
                            <div class="mb-3">
                                <label for="add-bot-code" class="form-label">代码</label>
                                <br/>
                                <VAceEditor
                                v-model:value="botadd.content"
                                @init="editorInit"
                                lang="c_cpp"
                                theme="textmate"
                                style="height: 300px" />
                            </div>
                        </div>
                        <div class="modal-footer">
                            <div class="error-message">{{ botadd.error_message }}</div>
                            <button type="button" class="btn btn-secondary btn_style" data-bs-dismiss="modal">关闭</button>
                            <button type="button" class="btn btn-primary btn_style" @click="add_bot">创建</button>
                        </div>
                        </div>
                    </div>
                    </div>
                </div>
                <div class="card-body">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="bot in bots" :key="bot.id">
                                <td>{{ bot.title }}</td>
                                <td>{{ bot.createtime }}</td>
                                <td>
                                    <button type="button" class="btn btn-info btn_style" data-bs-toggle="modal" :data-bs-target="'#update-bot-btn-' + bot.id" >修改</button>
                                    <button type="button" class="btn btn-danger btn_style" @click="remove_bot(bot)">删除</button>

                                    <div class="modal fade" :id="'update-bot-btn-' + bot.id">
                                    <div class="modal-dialog  modal-xl">
                                        <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5">添加机器人</h1>
                                            <button type="button" class="btn-close " data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                        <div class="mb-3">
                                                <label for="add-bot-title" class="form-label">名称</label>
                                                <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入名称">
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-description" class="form-label">简介</label>
                                                <br/>
                                                <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入简介"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-code" class="form-label">代码</label>
                                                <br/>
                                                <VAceEditor
                                                    v-model:value="bot.content"
                                                    @init="editorInit"
                                                    lang="c_cpp"
                                                    theme="textmate"
                                                    style="height: 300px" />
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <div class="error-message">{{ bot.error_message }}</div>
                                            <button type="button" class="btn btn-secondary btn_style" data-bs-dismiss="modal">关闭</button>
                                            <button type="button" class="btn btn-primary btn_style" @click="update_bot(bot)">保存修改</button>
                                        </div>
                                        </div>
                                    </div>
                                    </div>
                                
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script>

import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'

import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

export default {   
    components: {
        VAceEditor
    },
    setup() {
        ace.config.set(
    "basePath", 
    "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")
        const store = useStore();

        let bots = ref([]);

        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: ""
        })

        const refresh_bots = () => {
            $.ajax({
                url: "https://app4296.acapp.acwing.com.cn/api/user/bot/getlist",
                type: "get",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    bots.value = resp;
                },
            });  
        }

        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "https://app4296.acapp.acwing.com.cn/api/user/bot/add",
                type: "post",
                data:{
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    if(resp.error_message === "success"){
                        refresh_bots();
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        Modal.getInstance("#add-bot-btn").hide();
                    }else{
                        botadd.error_message = resp.error_message;
                    }
                },
                error(resp){
                    botadd.error_message = resp.error_message;
                }
            });
        }

        const update_bot = (bot) => {
            botadd.error_message = "";
            $.ajax({
                url: "https://app4296.acapp.acwing.com.cn/api/user/bot/update",
                type: "get",
                data:{
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    if(resp.error_message === "success"){
                        refresh_bots();
                        Modal.getInstance('#update-bot-btn-' + bot.id).hide();
                    }else{
                        bot.error_message = resp.error_message;
                    }
                },
                error(resp){
                    bot.error_message = resp.error_message;
                }
            });
        }


        const remove_bot = (bot) => {
             $.ajax({
                url: "https://app4296.acapp.acwing.com.cn/api/user/bot/remove",
                type: "get",
                data:{
                   bot_id: bot.id
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    if(resp.error_message === "success"){
                    
                        refresh_bots();
                    }else{
                        botadd.error_message = resp.error_message;
                    }
                },
                error(resp){
                    botadd.error_message = resp.error_message;
                }
            });
        }

        refresh_bots();
        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            VAceEditor
        }
    }
}
</script>
<style>
.btn_style{
    width: 25%;
    margin-right: 10px;
}

.error-message{
    color: red;
}

</style>

