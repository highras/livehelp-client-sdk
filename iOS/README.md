SDK 接入说明
=
系统要求
-

   *  支持系统iOS10以上

SDK支持
-
   *  包含armv7,armv7s,arm64,i386,x86_64指令集, 可运行真机 + 模拟器 

集成依赖
-

   *  引入头文件 #import <FPCustomerSDK/FPCustomerSDK.h>
   
   *  拖入 FPCustomerSDKResource 文件夹放根目录 （点击 create folder references ） 不要改名字！
    
   *  info.plist 
   
           添加  Privacy - Photo Library Usage Description  相册权限       
           添加  Privacy - Camera Usage Description  相机权限
         
    
接口
-

    1.初始化

    +(BOOL)fpCustomerInitWithProjectId:(NSInteger)projectId                                    //项目ID(客服控制台获取)    必传
                                userId:(NSString * _Nonnull)userId                             //用户ID                  必传
                            projectKey:(NSString * _Nonnull)projectKey                         //密匙(客服控制台获取)      必传
                          gameLanguage:(NSString * _Nonnull)gameLanguage                       //游戏语言(语言编码采用ISO 639-1标准)   必传
                                gameId:(NSString * _Nullable)gameId                            //游戏应用商店ID
                              userName:(NSString * _Nullable)userName                          //玩家游戏名称
                              serverId:(NSString * _Nullable)serverId                          //当前区服ID
                           networkType:(NSString * _Nullable)networkType                       //网络类型
                                  tags:(NSArray<NSString*> * _Nullable)tags                    //标签名列表(大小写敏感), 标签从客服控制台创建,设置->标签设置)
                              vipLevel:(NSInteger)vipLevel                                     //玩家VIP等级
                                custom:(NSDictionary * _Nullable)custom                        //自定义参数,传入信息会显示在控制台的客诉详情中
                                domain:(NSString * _Nonnull)domain                             //公司域名(与控制台一致)     必传
                       pushDeviceToken:(NSString * _Nullable)pushDeviceToken;                  //推送token
            
            
            
    2.faq调用
                                   
    if ([FPCustomerManager shareInstance].initFinish) {
    
        FPNavigationController * nav = [[FPNavigationController alloc] initWithRootViewController:[FPFaqTypeViewController new]];
        nav.modalPresentationStyle = UIModalPresentationFullScreen;
        [self presentViewController:nav animated:YES completion:nil];
        
    }
    
    
    
    3.智能机器人调用

    if ([FPCustomerManager shareInstance].initFinish) {
    
        FPCustomerSmartServiceViewController * vc = [FPCustomerSmartServiceViewController
                                                     initWithAppId:[FPCustomerManager shareInstance].projectId
                                                     appKey:[FPCustomerManager shareInstance].projectKey
                                                     userId:[FPCustomerManager shareInstance].userId
                                                     userName:[FPCustomerManager shareInstance].userName
                                                     gameId:[FPCustomerManager shareInstance].gameId
                                                     gameLang:[FPCustomerManager shareInstance].gameLanguage
                                                     vipLevel:[FPCustomerManager shareInstance].vipLevel];
    
        FPNavigationController * nav = [[FPNavigationController alloc] initWithRootViewController:vc];
        nav.modalPresentationStyle = UIModalPresentationFullScreen;
        [self presentViewController:nav animated:YES completion:nil];
    
    }
    
   

    4.拉取是否有未读
    
    [FPCustomerManager getUnreadStatus:^(BOOL result) {
        
    }];
