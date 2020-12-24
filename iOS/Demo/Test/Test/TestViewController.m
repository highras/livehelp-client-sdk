//
//  TestViewController.m
//  FPCustomerService
//
//  Created by zsl on 2020/7/17.
//  Copyright © 2020 FunPlus. All rights reserved.
//

#import "TestViewController.h"
#import <FPCustomerSDK/FPCustomerSDK.h>
@interface TestViewController ()<UITextFieldDelegate>
@property(nonatomic,strong)UITextField * language;
@property(nonatomic,strong)UITextField * name;
@property(nonatomic,strong)UITextField * domain;
@end

@implementation TestViewController
-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [self.view endEditing:YES];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    
    
    self.name = [[UITextField alloc] initWithFrame:CGRectMake(50, 100, 300, 40)];
    self.name.backgroundColor = [UIColor orangeColor];
    [self.view addSubview:self.name];
    self.name.placeholder = @"请输入UserID ";
    self.name.text = @"player00";
    
    
    self.language = [[UITextField alloc] initWithFrame:CGRectMake(50, 150, 300, 40)];
    self.language.backgroundColor = [UIColor orangeColor];
    [self.view addSubview:self.language];
    self.language.placeholder = @"请输入语言 ";
    self.language.text = @"zh-CN";
    
    
    self.domain = [[UITextField alloc] initWithFrame:CGRectMake(50, 200, 300, 40)];
    self.domain.backgroundColor = [UIColor orangeColor];
    [self.view addSubview:self.domain];
    self.domain.placeholder = @"请输入域名 ";
    self.domain.text = @"livedata";
    
    
    UIButton * ok = [UIButton buttonWithType:UIButtonTypeCustom];
    [ok setTitle:@"请先点击我初始化 \n" forState:UIControlStateNormal];
    ok.titleLabel.numberOfLines = 0;
    ok.titleLabel.font = [UIFont systemFontOfSize:18];
    ok.backgroundColor = [UIColor orangeColor];
    ok.frame = CGRectMake(100, 250, 180, 70);
    [ok addTarget:self action:@selector(okClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:ok];
    
    UIButton * smart = [UIButton buttonWithType:UIButtonTypeCustom];
    [smart setTitle:@"智能客服" forState:UIControlStateNormal];
    smart.backgroundColor = [UIColor orangeColor];
    smart.frame = CGRectMake(100, 330, 180, 80);
    [smart addTarget:self action:@selector(smartClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:smart];
    
    
    UIButton * faq = [UIButton buttonWithType:UIButtonTypeCustom];
    [faq setTitle:@"Faq" forState:UIControlStateNormal];
    faq.backgroundColor = [UIColor orangeColor];
    faq.frame = CGRectMake(100, 420, 180, 80);
    [faq addTarget:self action:@selector(faqClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:faq];
    
    UIButton * unread = [UIButton buttonWithType:UIButtonTypeCustom];
    [unread setTitle:@"获取是否有未读" forState:UIControlStateNormal];
    unread.backgroundColor = [UIColor orangeColor];
    unread.frame = CGRectMake(100, 510, 180, 80);
    [unread addTarget:self action:@selector(unreadlClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:unread];
    

    
}
-(void)okClick{
    //@"player00"
    BOOL result = [FPCustomerManager fpCustomerInitWithProjectId:80900001
                                                          userId:self.name.text
                                                      projectKey:@"61hMzMf0lNTnsccFKRbZGdA8E/qtT/O7HkujsYkaAE8="
                                                    gameLanguage:self.language.text
                                                          gameId:@"gameId"
                                                        userName:@"userName123"
                                                        serverId:@"serverId123"
                                                     networkType:@"wifi"
                                                            tags:@[@"tag1",@"tag2"]
                                                        vipLevel:111
                                                          custom:@{@"custom1":@"custom2"}
                                                          domain:self.domain.text
                                                 pushDeviceToken:@"pushDeviceToken"];
    
    if (result == NO) {
        UIAlertController *alertVc = [UIAlertController alertControllerWithTitle:@"提示" message:@"init error" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *cancelBtn = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull   action) {
        
        }];
        [alertVc addAction:cancelBtn];
        [self presentViewController:alertVc animated:YES completion:nil];
        return;
    }else{
        UIAlertController *alertVc = [UIAlertController alertControllerWithTitle:@"提示" message:@"init success" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *cancelBtn = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull   action) {
        
        }];
        [alertVc addAction:cancelBtn];
        [self presentViewController:alertVc animated:YES completion:nil];
        return;
    }
}
-(void)unreadlClick{
    [FPCustomerManager getUnreadStatus:^(BOOL result) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (result) {
                UIAlertController *alertVc = [UIAlertController alertControllerWithTitle:@"提示" message:@"有未读" preferredStyle:UIAlertControllerStyleAlert];
                UIAlertAction *cancelBtn = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull   action) {
                
                }];
                [alertVc addAction:cancelBtn];
                [self presentViewController:alertVc animated:YES completion:nil];
            }else{
                UIAlertController *alertVc = [UIAlertController alertControllerWithTitle:@"提示" message:@"无未读" preferredStyle:UIAlertControllerStyleAlert];
                UIAlertAction *cancelBtn = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull   action) {
                
                }];
                [alertVc addAction:cancelBtn];
                [self presentViewController:alertVc animated:YES completion:nil];
            }
        });
        
    }];
    
}
-(void)smartClick{
    
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
    
    
    
}
-(void)faqClick{
    if ([FPCustomerManager shareInstance].initFinish) {
        FPNavigationController * nav = [[FPNavigationController alloc] initWithRootViewController:[FPFaqTypeViewController new]];
        nav.modalPresentationStyle = UIModalPresentationFullScreen;
        [self presentViewController:nav animated:YES completion:nil];
    }
    
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

