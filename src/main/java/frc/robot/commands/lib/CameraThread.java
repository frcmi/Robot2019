package frc.robot.commands.lib;

import frc.robot.subsystems.Camera;

public class CameraThread extends Thread{
    private volatile boolean runtimeFlag;
    
    public void stopRunning(){
        runtimeFlag = false;
    }

    @Override
    public void run(){
        System.out.println("Starting camera forwarding thread");
        runtimeFlag = true;
        while(runtimeFlag){
            Camera.getInstance().forwardFrame();
        }
        System.out.println("Camera forwarding thread exited cleanly");
    }
}