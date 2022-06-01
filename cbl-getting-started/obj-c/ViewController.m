#import "ViewController.h"

#import <CouchbaseLite/CouchbaseLite.h>

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    NSError *error;
    CBLDatabase *database = [[CBLDatabase alloc] initWithName:@"mydb" error:&error];

    // ドキュメント作成
    CBLMutableDocument *mutableDoc = [[CBLMutableDocument alloc] init];
    [mutableDoc setString:@"佐藤" forKey:@"lastname"];
    [mutableDoc setString:@"user" forKey:@"type"];

    // ドキュメント保存
    [database saveDocument:mutableDoc error:&error];

    // ドキュメント取得、変更、保存
    CBLMutableDocument *mutableDoc2 =
        [[database documentWithID:mutableDoc.id] toMutable];
    [mutableDoc2 setString:@"太郎" forKey:@"firstname"];
    [database saveDocument:mutableDoc2 error:&error];


    CBLDocument *document = [database documentWithID:mutableDoc2.id];
    // ドキュメントIDとプロパティの出力
    NSLog(@"ドキュメントID: %@", document.id);
    NSLog(@"タイプ: %@", [document stringForKey:@"type"]);
    NSLog(@"ユーザー名: %@ %@", [document stringForKey:@"lastname"], [document stringForKey:@"firstname"]);

    // クエリ作成
    CBLQueryExpression *type =
        [[CBLQueryExpression property:@"type"] equalTo:[CBLQueryExpression string:@"user"]];
    CBLQuery *query = [CBLQueryBuilder select:@[[CBLQuerySelectResult all]]
                                        from:[CBLQueryDataSource database:database]
                                        where:type];

    // クエリ実行
    CBLQueryResultSet *result = [query execute:&error];
    NSLog(@"ユーザー数: %lu", (unsigned long)[[result allResults] count]);

    // データベースクローズ
    if (![database close:&error])
        NSLog(@"Error closing db:%@", error);

}


@end
