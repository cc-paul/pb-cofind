package com.pegp.eservicio;

import android.app.Application;

public class Links extends Application {
    String mainLink = "https://apps.project4teen.online/e-servicio-api/";
    String loginAPI = mainLink + "php/login.php";
    String OTPAPI = mainLink + "php/otp.php";
    String changePassAPI = mainLink + "php/forgotpass_change.php";
    String loacationAPI = mainLink + "php/location.php";
    String serviceAPI = mainLink + "php/service_type.php";
    String registerAPI = mainLink + "php/register.php";
    String profileApi = mainLink + "php/profile.php";
    String uploadImageApi = mainLink + "php/upload_image.php";
    String changePassAPI2 = mainLink + "php/change_pass.php";
    String profileOptionsAPI = mainLink + "php/image_options.php";
    String updateProfileAPI = mainLink + "php/update_profile.php";
    String saveScheduleAPI = mainLink + "php/save_schedule.php";
    String getScheduleAPI = mainLink + "php/get_schedule.php";
    String getScheduleDayAPI = mainLink + "php/get_schedule_day.php";
    String getProfilesAPI = mainLink + "php/get_profile.php";
    String getHiBidAPI = mainLink + "php/hibid.php";
    String acceptBid = mainLink + "php/accept_bid.php";
    String countNotif = mainLink + "php/count_notif.php";
    String getNotif = mainLink + "php/get_notiflist.php";
    String getReservation = mainLink + "php/get_reservation.php";
    String doneService = mainLink + "php/done_service.php";
    String chatGroupAPI = mainLink + "php/create_chat.php";
    String sendMessageAPI = mainLink + "php/send_chat.php";
    String chatAllAPI = mainLink + "php/chat_all.php";
    String chatOneAPI = mainLink + "php/chat_one.php";
    String getMessageListAPI = mainLink + "php/get_chatlist.php";
    String saveReportAPI = mainLink + "php/save_report.php";
    String customerPostAPI = mainLink + "php/customer_post.php";
    String customerFeedAPI = mainLink + "php/get_customer_post.php";

    public String sendReplyAPI = mainLink + "php/send_reply.php";
    public String sendMessageApi = mainLink + "php/send_message.php";
    public String sendCustomerPostApi = mainLink + "php/send_comment_customer.php";
    public String bidApi = mainLink + "php/bid.php";
    public String sendLikeApi = mainLink + "php/send_like.php";
    public String getCommentsApi = mainLink + "php/get_comments.php";
    public String getCommentsCustomerApi = mainLink + "php/get_comments_customer.php";
    public String deleteCommentApi = mainLink + "php/delete_message.php";
    public String deleteCommentApi2 = mainLink + "php/delete_message2.php";
    public String deleteCommentApiCustomer = mainLink + "php/delete_message.php";
    public String getLikerApi = mainLink + "php/get_likers.php";
    public String pubchatGroupAPI = chatGroupAPI;
}
