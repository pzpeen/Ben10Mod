package net.pzpeen.ben10mod.utils;

public class ModUtilities {

    public static class Cooldown {
        double lastTimeMillis;
        double cdTime;

        public Cooldown(double cdTime){
            this.cdTime = cdTime;
            this.lastTimeMillis = 0;
        }

        public void setLastTimeUsed(){
            this.lastTimeMillis = System.currentTimeMillis();
        }

        public boolean isCharged(){
            return (System.currentTimeMillis() - this.lastTimeMillis) >= this.cdTime;
        }

    }

    public static class TickTimer {

        private int remainingTicks = -1;
        private int maxTicks = 0;

        public void start(int ticks){
            this.maxTicks = ticks;
            this.remainingTicks = ticks;
        }

        public void tick(){
            if(this.remainingTicks >= 0){
                //System.out.println("DECREASING TICK");
                this.remainingTicks--;
            }
        }

        public boolean isActive(){
            return this.remainingTicks > 0;
        }

        public int getRemainingTicks(){
            return this.remainingTicks;
        }

        public float getProgress(){
            if(this.maxTicks <= 0) return 0.0f;
            return (float) this.remainingTicks / this.maxTicks;
        }

        public int getMaxTicks(){
            return this.maxTicks;
        }

        public void stop(){
            this.maxTicks = 0;
            this.remainingTicks = -1;
        }

    }

}
