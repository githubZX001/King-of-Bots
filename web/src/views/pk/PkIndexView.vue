<template>

  <PlayGround v-if="$store.state.pk.status === 'playing'"/>

  <MatchGround v-if="$store.state.pk.status === 'matching'"/>
  
  <ResultBoard v-if="$store.state.pk.loser != 'none'"/>

  <div class="user-color" v-if="$store.state.pk.status === 'playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)">左上角</div>
  <div class="user-color" v-if="$store.state.pk.status === 'playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)">右上角</div>
  
</template>

<script>
import PlayGround from '@/components/PlayGround.vue';
import MatchGround from '@/components/MatchGround.vue';
import ResultBoard from '@/components/ResultBoard.vue';
import { onMounted, onUnmounted} from 'vue';
import { useStore } from 'vuex';
export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,

    },
    setup(){
      const store = useStore();
      const socketUrl  = `wss://app4296.acapp.acwing.com.cn/websocket/${store.state.user.token}/`;

      let socket = null;

      store.commit("updateLoser", "none");
      store.commit("updateIsRecord", false);
      
      // 挂载
      onMounted(() => {
        store.commit("updateOpponent", {
          username: "我的对手",
          photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
        })
        // 建立一个WebSocket连接
        socket = new WebSocket(socketUrl);

        // 建立连接调用
        socket.onopen = () =>{
          console.log("connected!");
          store.commit("updateSocket", socket);
        }

        // 接受消息调用
        socket.onmessage = (msg) =>{
          const data = JSON.parse(msg.data);
          // console.log(data);
          if(data.event === "start-matching"){
            store.commit("updateOpponent", {
              username: data.opponent_username,
              photo: data.opponent_photo,
            });

            setTimeout(() => {
              store.commit("updateStatus", "playing");
            }, 200);

            store.commit("updateGame", data.game);
          }else if(data.event === "move"){
            const gamemap = store.state.pk.gameObject;
            const [snake0, snake1] = gamemap.snakes;
            snake0.set_direction(data.a_direction);
            snake1.set_direction(data.b_direction);
          }else if(data.event === "result"){
            const gamemap = store.state.pk.gameObject;
            const [snake0, snake1] = gamemap.snakes;

            store.commit("updateLoser", data.loser)

            if(data.loser === "all" || data.loser === "A"){
              snake0.status = "die";
            }

            if(data.loser === "all" || data.loser === "B"){
              snake1.status = "die";
            }

          }
        }

        // 关闭连接调用
        socket.onclose = () => {
          store.commit("updateStatus", "matching");
          console.log("disconnected!");
        }

      });

      // 卸载
      onUnmounted(() => {
        socket.close();
      });
    }

    
}
</script>
<style>  
div.user-color{
  text-align: center;
  width: 600;
  color: white;
  font-size: 30px;
}
</style>

