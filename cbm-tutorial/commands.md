```
git clone -b master --depth 1 https://github.com/couchbaselabs/mobile-travel-sample.git
```

```
docker network create -d bridge workshop
```

```
docker pull couchbase/server-sandbox:7.0.0
```


```
docker run -d --name cb-server --network workshop -p 8091-8094:8091-8094 -p 11210:11210 couchbase/server-sandbox:7.0.0
```

```
docker ps
```

```
docker logs cb-server
```

```
docker pull couchbase/sync-gateway:3.0.0-enterprise
```

```
docker run -p 4984-4985:4984-4985 --network workshop --name sync-gateway -d -v `pwd`/sync-gateway-config-travelsample.json:/etc/sync_gateway/sync_gateway.json couchbase/sync-gateway:3.0.0-enterprise -adminInterface :4985 /etc/sync_gateway/sync_gateway.json
```

```
docker run -p 4984-4985:4984-4985 --network workshop --name sync-gateway -d -v %cd%/sync-gateway-config-travelsample.json:/etc/sync_gateway/sync_gateway.json couchbase/sync-gateway:3.0.0-enterprise -adminInterface :4985 /etc/sync_gateway/sync_gateway.json
```

```
docker logs sync-gateway
```

```
docker pull connectsv/try-cb-python-v2:6.5.0-server
```

```
docker run -it -p 8080:8080 --network workshop --name try-cb-python connectsv/try-cb-python-v2:6.5.0-server
```

```
curl -X GET http://localhost:4984/travel-sample/
```

```
echo -n "demo:password" | base64
```


```
curl -X GET http://localhost:4984/travel-sample/ -H 'authorization: Basic ZGVtbzpwYXNzd29yZA=='
```

```
docker stop cb-server sync-gateway
```

```
docker rm cb-server sync-gateway try-cb-python
```

```
docker rmi couchbase/server-sandbox:7.0.0 couchbase/sync-gateway:3.0.0-enterprise connectsv/try-cb-python-v2:6.5.0-server
```

