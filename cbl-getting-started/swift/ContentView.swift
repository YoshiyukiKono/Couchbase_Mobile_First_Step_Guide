import SwiftUI
import CouchbaseLiteSwift


struct ContentView: View {
    var body: some View {
        Text("Hello, world!")
            .padding()
            .onAppear {
                getStarted ()
            }
    }
    
    func getStarted () {
        // データベース作成または取得（再実行時）
        let database: Database
        do {
            database = try Database(name: "mydb")
        } catch {
            fatalError("Error opening database")
        }

        // ドキュメント作成
        let mutableDoc = MutableDocument().setString("佐藤", forKey: "lastname")
            .setString("user", forKey: "type")

        // ドキュメント保存
        do {
            try database.saveDocument(mutableDoc)
        } catch {
            fatalError("Error saving document")
        }

        // ドキュメント取得、変更、保存
        if let mutableDoc = database.document(withID: mutableDoc.id)?.toMutable() {
            mutableDoc.setString("太郎", forKey: "firstname")
            do {
                try database.saveDocument(mutableDoc)

                let document = database.document(withID: mutableDoc.id)!
                print("ドキュメントID: \(document.id)!)")
                print("名前： \(document.string(forKey: "lastname")!) \(document.string(forKey: "firstname")!)")
            } catch {
                fatalError("Error updating document")
            }
        }

        // クエリ
        print("クエリ実行")
        let query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.database(database))
            .where(Expression.property("type").equalTo(Expression.string("user")))

        do {
            let result = try query.execute()
            print("ユーザー数: \(result.allResults().count)")
        } catch {
            fatalError("Error running the query")
        }

        do {
            try database.close()
        } catch {
            fatalError("Error running the query")
        }
    }

    
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
