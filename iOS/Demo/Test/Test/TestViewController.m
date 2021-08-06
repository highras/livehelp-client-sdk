//
//  TestViewController.m
//  FPCustomerService
//
//  Created by zsl on 2020/7/17.
//  Copyright © 2020 FunPlus. All rights reserved.
//

#import "TestViewController.h"
#import "Masonry.h"
#import "IQKeyboardManager.h"
#import <Livehelp/Livehelp.h>

@interface TestViewController ()<UITextFieldDelegate>
@property(nonatomic,strong)UITextField * language;
@property(nonatomic,strong)UITextField * name;
@property(nonatomic,strong)UITextField * domain;
@property(nonatomic,strong)UITextField * userId;
@property(nonatomic,strong)UITextField * projectId;
@property(nonatomic,strong)UITextField * key;
@end

@implementation TestViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    
    UIScrollView * backScrollView = [[UIScrollView alloc] init];
    backScrollView.backgroundColor = [UIColor colorWithRed:30./255 green:82.0/255 blue:199.0/255 alpha:1];
    [self.view addSubview:backScrollView];
    [backScrollView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.top.bottom.equalTo(self.view);
        make.width.equalTo(@(self.view.frame.size.width));
    }];
    
    UIImageView * logo = [UIImageView new];
    logo.image = [UIImage imageNamed:@"logo.png"];
    [backScrollView addSubview:logo];
    [logo mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(@(50));
        make.top.equalTo(@(30));
        make.width.equalTo(@(150));
    }];
    logo.contentMode = UIViewContentModeScaleAspectFit;
    
    int w = self.view.frame.size.width - 100;
    int h = 35;
    int hSpace = 35;
    
    self.userId = [self getTextFieldWithName:@"用户ID"];
    [backScrollView addSubview:self.userId];
    [self.userId mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(logo.mas_bottom).offset(hSpace);
        make.width.equalTo(@(w));
        make.height.equalTo(@(h));
        make.centerX.equalTo(self.view);
    }];
    self.userId.text = @"player00";
//
    self.projectId = [self getTextFieldWithName:@"项目ID"];
    [backScrollView addSubview:self.projectId];
    [self.projectId mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.userId.mas_bottom).offset(hSpace);
        make.width.equalTo(@(w));
        make.height.equalTo(@(h));
        make.centerX.equalTo(self.view);
    }];
    self.projectId.text = @"80900001";

    self.domain = [self getTextFieldWithName:@"项目域名"];
    [backScrollView addSubview:self.domain];
    [self.domain mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.projectId.mas_bottom).offset(hSpace);
        make.width.equalTo(@(w));
        make.height.equalTo(@(h));
        make.centerX.equalTo(self.view);
    }];
    self.domain.text = @"livedata";

    self.key = [self getTextFieldWithName:@"项目key"];
    [backScrollView addSubview:self.key];
    [self.key mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.domain.mas_bottom).offset(hSpace);
        make.width.equalTo(@(w));
        make.height.equalTo(@(h));
        make.centerX.equalTo(self.view);
    }];
    self.key.text = @"61hMzMf0lNTnsccFKRbZGdA8E/qtT/O7HkujsYkaAE8=";
    
    self.language = [self getTextFieldWithName:@"项目语言 (例如:zh-CN)"];
    [backScrollView addSubview:self.language];
    [self.language mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.key.mas_bottom).offset(hSpace);
        make.width.equalTo(@(w));
        make.height.equalTo(@(h));
        make.centerX.equalTo(self.view);
    }];
    self.language.text = @"zh-CN";
//    lang.text = @"zh-CN";
    
    
    UIButton * customerServiceBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    customerServiceBtn.layer.masksToBounds = YES;
    customerServiceBtn.layer.cornerRadius = 5;
    [customerServiceBtn setTitle:@"客服初始化" forState:UIControlStateNormal];
    customerServiceBtn.titleLabel.font = [UIFont systemFontOfSize:20];
    customerServiceBtn.backgroundColor = [UIColor colorWithRed:69./255 green:159.0/255 blue:248.0/255 alpha:1];
    [customerServiceBtn addTarget:self action:@selector(okClick) forControlEvents:UIControlEventTouchUpInside];
    [backScrollView addSubview:customerServiceBtn];
    [customerServiceBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.language.mas_bottom).offset(hSpace + 20);
        make.left.equalTo(self.userId);
        make.width.equalTo(@(w/2 - 20));
        make.height.equalTo(@(50));
    }];
    
    UIButton * questionBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    questionBtn.layer.masksToBounds = YES;
    questionBtn.layer.cornerRadius = 5;
    [questionBtn setTitle:@"客服Faq" forState:UIControlStateNormal];
    questionBtn.titleLabel.font = [UIFont systemFontOfSize:20];
    questionBtn.backgroundColor = [UIColor colorWithRed:69./255 green:159.0/255 blue:248.0/255 alpha:1];
    [questionBtn addTarget:self action:@selector(faqClick) forControlEvents:UIControlEventTouchUpInside];
    [backScrollView addSubview:questionBtn];
    [questionBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.language.mas_bottom).offset(hSpace + 20);
        make.right.equalTo(self.userId);
        make.width.equalTo(@(w/2 - 20));
        make.height.equalTo(@(50));
        make.bottom.equalTo(backScrollView).offset(-400);
    }];
    
    
    
    UILabel * company = [UILabel new];
    [self.view addSubview:company];
    company.font = [UIFont systemFontOfSize:12];
    company.textColor = [UIColor colorWithRed:190.0/255 green:199.0/255 blue:212.0/255 alpha:1];
    company.numberOfLines = 0;
    company.textAlignment = NSTextAlignmentCenter;
    company.text = @"© 2021 北京云上曲率科技有限公司 \nAll Rights Reserved.京ICP备18051523号";
    [company mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.userId);
        make.width.equalTo(self.userId);
        make.bottom.equalTo(self.view).offset(-40);
    }];
    
    
}
-(UITextField*)getTextFieldWithName:(NSString*)name{
    UITextField * textField = [[UITextField alloc] init];
    textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    textField.font = [UIFont systemFontOfSize:18];
    textField.textColor = [UIColor whiteColor];
    NSAttributedString *attrString = [[NSAttributedString alloc] initWithString:name
                                                                     attributes:
        @{NSForegroundColorAttributeName:[UIColor colorWithRed:190.0/255 green:199.0/255 blue:212.0/255 alpha:1],
                     NSFontAttributeName:textField.font
             }];
    textField.attributedPlaceholder = attrString;
    
    UIView * line = [[UIView alloc] init];
    line.backgroundColor = [UIColor whiteColor];
    [textField addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.bottom.equalTo(textField);
        make.left.equalTo(textField).offset(-5);
        make.height.equalTo(@(1.0));
    }];
    
    return textField;
}
-(void)okClick{
    //@"player00"
    BOOL result = [LivehelpSupport initWithAppId:[self.projectId.text intValue]
                                         secretKey:self.key.text
                                            domain:self.domain.text
                                          language:self.language.text];
    
    if (result) {
        [LivehelpSupport resetUserInfoWithUserId:self.userId.text userName:@"userName123" avatar:@"avatar" language:self.language.text email:@"email" tags:[NSArray array] customData:[NSDictionary dictionary] deviceToken:@"deviceToken" resetResult:^(BOOL isSuccess) {
    
            
            [LivehelpSupport setLanguage:@"en"];
            
            
        }];
    }
    
}

-(void)faqClick{
    
    LivehelpSupportNavigationController * nav = [LivehelpSupport showAllFAQs];
    if (nav != nil) {
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

