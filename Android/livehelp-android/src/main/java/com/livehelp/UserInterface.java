package com.livehelp;

public class UserInterface {
    public interface ErrorRecorder {
        public  void recordError(String message) ;

        public  void recordError(Exception e) ;

        public  void recordError(String message, Exception e) ;
    }

    /**
     * 未读消息回调
     */
    public interface IUnreadCallback {
        void getMsg(String errmsg, int unreadCount);// errmsg-接口返回是否成功 若成功 errmsg为空， unreadCount-未读消息数量
    }


    /**
     * 用户回调
     */
    public interface IUserCallback{
        void onResult(String errmsg); //若成功 errmsg为空
    }


    public enum ConversationType{
        BOT,
        HUMAN
    }
}
