//
//  RCCSLeaveMessagesCell.m
//  RongIMKit
//
//  Created by 张改红 on 2016/12/5.
//  Copyright © 2016年 RongCloud. All rights reserved.
//

#import "RCCSLeaveMessagesCell.h"
#import "RCKitCommonDefine.h"
@interface RCCSLeaveMessagesCell()<UITextFieldDelegate,UITextViewDelegate>
@property (nonatomic,strong) UILabel *textNum;
@property (nonatomic,assign) int max;
@property (nonatomic,copy) NSString *alertText;
@property (nonatomic,strong) UITextView *placeHolderText;
@end
@implementation RCCSLeaveMessagesCell
- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
  self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
  if (self) {
    [self setupSubviews];
  }
  return self;
}

- (void)setupSubviews{
  self.titleLabel = [[UILabel alloc] init];
  self.titleLabel.font = [UIFont systemFontOfSize:16];
  
  self.infoTextField = [[UITextField alloc] init];
  self.infoTextField.clearButtonMode = UITextFieldViewModeWhileEditing;
  self.infoTextField.font = [UIFont systemFontOfSize:16];
  self.infoTextField.delegate = self;
  
  self.infoTextView = [[UITextView alloc] init];
  self.infoTextView.font = [UIFont systemFontOfSize:16];
  self.infoTextView.delegate = self;
  self.infoTextView.backgroundColor = [UIColor clearColor];
  self.placeHolderText = [[UITextView alloc] init];
  self.placeHolderText.font = [UIFont systemFontOfSize:16];
  self.placeHolderText.editable = NO;
  self.placeHolderText.textColor = RGBCOLOR(188, 188, 194);
  
  self.textNum = [[UILabel alloc] init];
  self.textNum.font = [UIFont systemFontOfSize:12];
  self.textNum.textColor = HEXCOLOR(0x999999);
  self.textNum.textAlignment = NSTextAlignmentRight;
  
  [self.contentView addSubview:self.titleLabel];
  [self.contentView addSubview:self.infoTextField];
  [self.contentView addSubview:self.placeHolderText];
  [self.contentView addSubview:self.infoTextView];
  [self.contentView addSubview:self.textNum];

  self.titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
  self.infoTextField.translatesAutoresizingMaskIntoConstraints = NO;
  self.infoTextView.translatesAutoresizingMaskIntoConstraints = NO;
  self.placeHolderText.translatesAutoresizingMaskIntoConstraints = NO;
  self.textNum.translatesAutoresizingMaskIntoConstraints = NO;
}

- (void)setDataWithModel:(RCCSLeaveMessageItem *)model{
  self.titleLabel.text = model.title;
  if ([model.type isEqualToString:@"text"]) {
    [self setLayoutConstraint:NO];
    self.infoTextField.placeholder = model.defaultText;
  }else if ([model.type isEqualToString:@"textarea"]){
    [self setLayoutConstraint:YES];
    self.placeHolderText.text = model.defaultText;
    if (model.message.count == 3) {
      self.alertText = model.message[2];
    }
  }
  self.max = model.max;
  self.textNum.text = [NSString stringWithFormat:@"%ld/%d",0,self.max];
}

- (void)setLayoutConstraint:(BOOL)isMultiLine{
  NSDictionary *views = NSDictionaryOfVariableBindings(_titleLabel,_infoTextField,_infoTextView,_placeHolderText,_textNum);
  if (isMultiLine) {
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-10-[_titleLabel(17)]-10-[_infoTextView]-7-[_textNum(13)]-10-|" options:0 metrics:nil views:views]];
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[_infoTextView]-10-|" options:0 metrics:nil views:views]];
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[_titleLabel]-10-|" options:0 metrics:nil views:views]];
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[_placeHolderText]-10-|" options:0 metrics:nil views:views]];
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[_textNum(100)]-10-|" options:0 metrics:nil views:views]];
    [self.contentView addConstraint:[NSLayoutConstraint constraintWithItem:self.placeHolderText attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.infoTextView attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    [self.contentView addConstraint:[NSLayoutConstraint constraintWithItem:self.placeHolderText attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.infoTextView attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
    [self.infoTextField removeFromSuperview];
  }else{
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[_titleLabel(35)]-5-[_infoTextField]-3.5-|" options:0 metrics:nil views:views]];
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[_infoTextField(20)]" options:0 metrics:nil views:views]];
    [self.contentView addConstraint:[NSLayoutConstraint constraintWithItem:self.infoTextField attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.titleLabel attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
    [self.contentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[_titleLabel(20)]" options:0 metrics:nil views:views]];
    [self.infoTextView removeFromSuperview];
    [self.placeHolderText removeFromSuperview];
    [self.textNum removeFromSuperview];
  }
  
  [self.contentView addConstraint:[NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1 constant:10]];
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
  
  UITextRange *selectedRange = [textView markedTextRange];
  //获取高亮部分
  UITextPosition *pos = [textView positionFromPosition:selectedRange.start offset:0];

  //如果有高亮且当前字数开始位置小于最大限制时允许输入
  if (selectedRange && pos) {
    NSInteger startOffset = [textView offsetFromPosition:textView.beginningOfDocument toPosition:selectedRange.start];
    NSInteger endOffset = [textView offsetFromPosition:textView.beginningOfDocument toPosition:selectedRange.end];
    NSRange offsetRange = NSMakeRange(startOffset, endOffset - startOffset);
    
    if (offsetRange.location <= self.max) {
      return YES;
    }else{
      return NO;
    }
  }
  if (self.max > 0 && (textView.text.length >self.max || range.location >= self.max || ((textView.text.length == self.max && text.length > 0))) ){
    UIAlertView *alertview = [[UIAlertView alloc] initWithTitle:nil message:self.alertText delegate:nil cancelButtonTitle:nil otherButtonTitles:@"确定", nil];
    [alertview show];
    self.textNum.text = [NSString stringWithFormat:@"%ld/%d",textView.text.length,self.max];
    return NO;
  }
  if (![text isEqualToString:@""]){
    self.placeHolderText.hidden = YES;
  }
  
  if ([text isEqualToString:@""] && range.location == 0 && range.length == 1){
    self.placeHolderText.hidden = NO;
  }
  return YES;
}

- (void)textViewDidChange:(UITextView *)textView{
  if (![textView.text isEqualToString:@""]){
    self.placeHolderText.hidden = YES;
  }
  
  UITextRange *selectedRange = [textView markedTextRange];
  //获取高亮部分
  UITextPosition *pos = [textView positionFromPosition:selectedRange.start offset:0];
  
  //如果在变化中是高亮部分在变，就不要计算字符了
  if (selectedRange && pos) {
    return;
  }

  if (self.max > 0 && textView.text.length > self.max){
    textView.text = [textView.text substringToIndex:self.max];
    UIAlertView *alertview = [[UIAlertView alloc] initWithTitle:nil message:self.alertText delegate:nil cancelButtonTitle:nil otherButtonTitles:@"确定", nil];
    [alertview show];
  }
  self.textNum.text = [NSString stringWithFormat:@"%ld/%d",textView.text.length,self.max];
}

- (void)textViewDidEndEditing:(UITextView *)textView{
  if (![textView.text isEqualToString:@""]){
    self.placeHolderText.hidden = YES ;
  }
}
@end
