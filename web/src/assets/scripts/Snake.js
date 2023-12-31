import { AcGameObject  } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject{
    constructor(info, gamemap){
        super(); 

        // info中传过来的就是一些蛇格子的信息
        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        // 存放蛇的身体，cells[0]存放蛇头
        this.cells = [new Cell(info.r, info.c)];
        // 下一步蛇走的目的地
        this.next_cell = null; 

        // 蛇每秒走5个格子
        this.speed = 5; 

        // -1表示没有指令，0、1、2、3表示上右下左
        this.direction = -1;

        // idle表示静止，move表示正在移动，die表示失败
        this.status = "idle";

        // 定义一下蛇移动的偏移量
        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1];

        // 表示回合数
        this.step = 0;

        // 误差
        this.eps = 1e-2;

        this.eye_direction = 0;
        // 左下角的蛇初始朝上，右下角的蛇朝下
        if(this.id === 1) this.eye_direction = 2;

        // 蛇眼睛不同方向的偏移量
        this.eye_dx = [
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1]
        ]; 
        this.eye_dy = [
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1]
        ];

    }

    start(){

    }

    set_direction(d){
        this.direction = d;
    }

    // 判断当前回合，蛇的长度是否增加
    check_tail_increasing(){
        if(this.step <= 10) return true;
        if(this.step % 3 === 1) return true;
        return false;

    }

    // 将蛇的状态变为走下一步
    next_step(){
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;
        this.status = "move";
        this.step ++;

        const k = this.cells.length;
        
        // 整体向后挪了一个位置，相当于变相的长出了一个新的头
        for(let i = k ; i > 0; i--){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));  
        }
    }

    update_move(){
        // 两帧之间 x方向偏移量 和 y方向偏移量 和  两点间的距离
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance < this.eps){
            // 添加一个新的蛇头
            this.cells[0] = this.next_cell;
            this.next_cell = null;
            this.status = "idle";

            if(!this.check_tail_increasing()){
                this.cells.pop();
            }
        }else{
            // 每两帧之间走的距离
            const move_distance = this.speed * this.timedelta / 1000;
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if(!this.check_tail_increasing()){
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }

    }

    update(){ 
        if(this.status == 'move'){
            this.update_move();
        }
        
        this.render();
    }

    render(){ 
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        
        ctx.fillStyle = this.color;

        if(this.status == "die"){
            ctx.fillStyle = "white";
        }
        
        for(const cell of this.cells){
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        // 给蛇的身体加上一些矩形，看起来更加圆润
        for(let i = 1; i < this.cells.length; i++){
            const a =  this.cells[i - 1], b=  this.cells[i];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps){
                continue;
            }
            // 身体的竖直的情况
            if(Math.abs(a.x - b.x) < this.eps){
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            }else{
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        // 画眼睛
        ctx.fillStyle = "black";
        for(let i = 0; i < 2; i++){
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.20) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.20) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.08, 0, Math.PI * 2);
            ctx.fill();
        }
    }

}