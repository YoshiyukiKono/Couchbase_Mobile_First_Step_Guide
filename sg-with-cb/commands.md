```
curl -vX  PUT "http://localhost:4985/mybucket/_config"   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='   -H 'Content-Type: application/json'  -H "accept: application/json"   -d '{ "enable_shared_bucket_access" : true, "import_docs": true }' 
```

```
curl -X PUT "http://localhost:4985/mybucket/_config/import_filter"   -H "accept: application/json"   -H "Content-Type: application/javascript"   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='   -d 'function(doc) { if (doc.type != "mobile") { return false; } return true; }'
```
