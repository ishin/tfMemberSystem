
#import <UIKit/UIKit.h>



@interface NSDate (Helper) {
}

//返回一个月会横跨几周
- (int )getWeekNumOfMonth;

//返回这个月的第一天
- (NSDate *)beginningOfMonth;

//一个月的最后一天
- (NSDate *)endOfMonth;

//一年中的第几周
- (int )getWeekOfYear;

//返回当前天的年月日.
- (NSDate *)beginningOfDay;

//返回一个星期中的第几天
- (NSUInteger)weekday;


//返回天的前后day天的日期类
- (NSDate *)dateAfterDay:(int)day;


- (NSDate *)dateafterMonth:(int)month;

- (NSUInteger)getDay;

- (NSUInteger)getMonth;
- (NSUInteger)getYear;

- (int )getHour;

- (int)getMinute;

- (int )getHour:(NSDate *)date ;
- (int)getMinute:(NSDate *)date;

- (NSUInteger)daysAgo;

- (NSUInteger)daysAgoAgainstMidnight;

- (NSString *)stringDaysAgo;

- (NSString *)stringDaysAgoAgainstMidnight:(BOOL)flag;


+ (NSDate *)dateFromString:(NSString *)string ;

+ (NSDate *)dateFromString:(NSString *)string withFormat:(NSString *)format;

+ (NSString *)stringFromDate:(NSDate *)date withFormat:(NSString *)format;

+ (NSString *)stringFromDate:(NSDate *)date;

+ (NSString *)stringForDisplayFromDate:(NSDate *)date prefixed:(BOOL)prefixed;
+ (NSString *)stringForDisplayFromDate:(NSDate *)date;

- (NSString *)stringWithFormat:(NSString *)format;

- (NSString *)string;

- (NSString *)stringWithDateStyle:(NSDateFormatterStyle)dateStyle timeStyle:(NSDateFormatterStyle)timeStyle;



//返回周日的的开始时间
- (NSDate *)beginningOfWeek;






//返回当前周的周末
- (NSDate *)endOfWeek;
+ (NSString *)dateFormatString;
+ (NSString *)timeFormatString ;
+ (NSString *)timestampFormatString;
// preserving for compatibility
+ (NSString *)dbFormatString;

@end