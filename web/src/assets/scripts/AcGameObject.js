const AC_GAME_OBJECTS = [];

export class AcGameObject {
    constructor(){
        AC_GAME_OBJECTS.push(this); 
        this.timedelta = 0;  // 两帧之间的时间间隔
        this.has_called_start = false; // 记录是否执行过
    }

    start() { // 只执行一次

    }

    update(){ // 每一帧执行一次，除了第一帧之外

    }

    on_destroy(){ // 删除之前执行
        
    }

    destroy(){
        this.on_destroy();

        // 用in是遍历下标
        for(let i in AC_GAME_OBJECTS){
            const obj = AC_GAME_OBJECTS[i];
            if(obj == this){
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

let last_timestamp; // 上一次执行的时刻

const step = (timedelta) =>{
    // 用of是遍历值
    for(let obj of AC_GAME_OBJECTS){
        // 如果这个没有没有执行过就开启一下
        if(!obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }else{
            obj.timedelta = timedelta - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timedelta;

    requestAnimationFrame(step);
}

requestAnimationFrame(step);