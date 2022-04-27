using System;

using Couchbase.Lite;
using Couchbase.Lite.Query;

namespace cbl3test
{
    class Program
    {
        static void Main(string[] args)
        {

            // データベース利用開始 (存在しない場合は、新規作成)
            Console.WriteLine("データベース利用開始");
            var database = new Database("mydb");

            // ドキュメント作成・保存
            string id = null;
            using (var mutableDoc = new MutableDocument())
            {
                mutableDoc.SetString("lastname", "佐藤")
                    .SetString("type", "user");

                database.Save(mutableDoc);
                id = mutableDoc.Id;
            }

            // ドキュメント取得・更新
            using (var doc = database.GetDocument(id))
            using (var mutableDoc = doc.ToMutable())
            {
                mutableDoc.SetString("firstname", "太郎");
                database.Save(mutableDoc);

                using (var docAgain = database.GetDocument(id))
                {
                    Console.WriteLine($"ドキュメントID: {docAgain.Id}");
                    Console.WriteLine($"名前: {docAgain.GetString("lastname")} {docAgain.GetString("firstname")}");
                }
            }

            // クエリ実行
            using (var query = QueryBuilder.Select(SelectResult.All())
                .From(DataSource.Database(database))
                .Where(Expression.Property("type").EqualTo(Expression.String("user"))))
            {
                // Run the query
                var result = query.Execute();
                Console.WriteLine($"ユーザー数: {result.AllResults().Count}");
            }

            // データベースクローズ
            database.Close();
        
        }
    }
}
