#include "cbl/CouchbaseLite.h"

int main(void) {
    CBLError err;
    CBLDatabase* database = CBLDatabase_Open(FLSTR("mydb"), NULL, &err);

    if(!database) {
        fprintf(stderr, "Error opening database (%d / %d)\n", err.domain, err.code);
        FLSliceResult msg = CBLError_Message(&err);
        fprintf(stderr, "%.*s\n", (int)msg.size, (const char *)msg.buf);
        FLSliceResult_Release(msg);
        return 1;
    }

    // ドキュメント作成
    CBLDocument* mutableDoc = CBLDocument_Create();
    FLMutableDict properties = CBLDocument_MutableProperties(mutableDoc);
	FLMutableDict_SetString(properties, FLSTR("type"), FLSTR("user"));
    FLMutableDict_SetString(properties, FLSTR("lastname"), FLSTR("佐藤"));

    // ドキュメント保存
    CBLDatabase_SaveDocument(database, mutableDoc, &err);
    if(!CBLDatabase_SaveDocument(database, mutableDoc, &err)) {
        fprintf(stderr, "Error saving a document (%d / %d)\n", err.domain, err.code);
        FLSliceResult msg = CBLError_Message(&err);
        fprintf(stderr, "%.*s\n", (int)msg.size, (const char *)msg.buf);
        FLSliceResult_Release(msg);
        return 1;
    }

    // ドキュメントIDを保存し、ドキュメントのメモリを解放
    // (注. FLSliceResultやFLStringResultとして確保した変数は明示的な解放が必要）
    FLStringResult id = FLSlice_Copy(CBLDocument_ID(mutableDoc));
    CBLDocument_Release(mutableDoc);

    // ドキュメント取得
    mutableDoc = 
        CBLDatabase_GetMutableDocument(database, FLSliceResult_AsSlice(id), &err);
    if(!mutableDoc) {
        fprintf(stderr, "Error getting a document (%d / %d)\n", err.domain, err.code);
        FLSliceResult msg = CBLError_Message(&err);
        fprintf(stderr, "%.*s\n", (int)msg.size, (const char *)msg.buf);
        FLSliceResult_Release(msg);
        return 1;
    }

    // ドキュメント更新・保存
    properties = CBLDocument_MutableProperties(mutableDoc);
    FLMutableDict_SetString(properties, FLSTR("firstname"), FLSTR("太郎"));
    if(!CBLDatabase_SaveDocument(database, mutableDoc, &err)) {
        fprintf(stderr, "Error saving a document (%d / %d)\n", err.domain, err.code);
        FLSliceResult msg = CBLError_Message(&err);
        fprintf(stderr, "%.*s\n", (int)msg.size, (const char *)msg.buf);
        FLSliceResult_Release(msg);
        return 1;
    }

    // リードオンリーでドキュメントを取得（注. constを指定しています)
    const CBLDocument* docAgain = 
        CBLDatabase_GetDocument(database, FLSliceResult_AsSlice(id), &err);
    if(!docAgain) {
        fprintf(stderr, "Error getting a document (%d / %d)\n", err.domain, err.code);
        FLSliceResult msg = CBLError_Message(&err);
        fprintf(stderr, "%.*s\n", (int)msg.size, (const char *)msg.buf);
        FLSliceResult_Release(msg);
        return 1;
    }

    // ここでは、コピーを行なっていないため、後のメモリ解放は不要 (注.下記ではFLStringを利用し、FLStringResultでないことに留意)
    FLString retrievedID = CBLDocument_ID(docAgain);
    FLDict retrievedProperties = CBLDocument_Properties(docAgain);
    FLString retrievedType = FLValue_AsString(FLDict_Get(retrievedProperties, FLSTR("type")));
    printf("ドキュメントID: %.*s\n", (int)retrievedID.size, (const char *)retrievedID.buf);
    printf("タイプ: %.*s\n", (int)retrievedType.size, (const char *)retrievedType.buf);

    CBLDocument_Release(mutableDoc);
    CBLDocument_Release(docAgain);
    FLSliceResult_Release(id);

    // クエリ作成と実行
    int errorPos;
    CBLQuery* query = CBLDatabase_CreateQuery(database, kCBLN1QLLanguage, FLSTR("SELECT * FROM _ WHERE type = \"user\""), &errorPos, &err);
    if(!query) {
        fprintf(stderr, "Error creating a query (%d / %d)\n", err.domain, err.code);
        FLSliceResult msg = CBLError_Message(&err);
        fprintf(stderr, "%.*s\n", (int)msg.size, (const char *)msg.buf);
        FLSliceResult_Release(msg);
        return 1;
    }
    CBLResultSet* results = CBLQuery_Execute(query, &err);
    if(!results) {
        fprintf(stderr, "Error executing a query (%d / %d)\n", err.domain, err.code);
        FLSliceResult msg = CBLError_Message(&err);
        fprintf(stderr, "%.*s\n", (int)msg.size, (const char *)msg.buf);
        FLSliceResult_Release(msg);
        return 1;
    }
    while(CBLResultSet_Next(results)) {
        FLDict dict = FLValue_AsDict(CBLResultSet_ValueForKey(results, FLSTR("_")));

        FLString firstname = FLValue_AsString(FLDict_Get(dict, FLSTR("firstname")));
		FLString lastname = FLValue_AsString(FLDict_Get(dict, FLSTR("lastname")));

        printf("名前: %.*s", (int)lastname.size, (const char *)lastname.buf);
		printf(" %.*s\n", (int)firstname.size, (const char *)firstname.buf);
    }

    CBLResultSet_Release(results);
    CBLQuery_Release(query);

    // データベースクローズ
    CBLDatabase_Close(database, &err);

    return 0;
}
