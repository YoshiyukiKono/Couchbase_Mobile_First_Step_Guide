```
./bin/sync_gateway ./basic.json
```

```
echo -n "Administrator:password" | base64
```

```
curl -X GET http://localhost:4985 -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='
```

```
curl --location --request PUT 'http://localhost:4985/mybucket/'  -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA==' --header 'Content-Type: application/json' --data-raw '{ "bucket": "mybucket","num_index_replicas": 0}'
```

```
curl -vX POST "http://localhost:4985/mybucket/_user/"   -H "accept: application/json" -H "Content-Type: application/json"   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='   -d '{"name": "Edge1User", "password": "pass"}'
```

```
curl -vX POST "http://localhost:4985/mybucket/_role/"   -H "accept: application/json" -H "Content-Type: application/json"   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='   -d '{"name": "Edge1"}' 
```

```
curl -vX PUT "http://localhost:4985/mybucket/_user/Edge1User"   -H "accept: application/json" -H "Content-Type: application/json"   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='   -d '{ "admin_roles": ["Edge1"], "admin_channels": ["Channel1"]}' 
```

```
curl -vX PUT "http://localhost:4985/mybucket/_role/Edge1"   -H "accept: application/json" -H "Content-Type: application/json"   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='   -d '{ "admin_channels": ["Channel2","Channel3"]}' 
```


```

```








```
curl http://localhost:4985/mybucket/_user/Edge1User   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='
```

```
curl http://localhost:4985/mybucket/_role/Edge1   -H 'authorization: Basic QWRtaW5pc3RyYXRvcjpwYXNzd29yZA=='
```
