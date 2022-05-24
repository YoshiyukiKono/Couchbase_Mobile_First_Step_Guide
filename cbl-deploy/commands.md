
```
ps | grep sync_gateway | grep -v grep | cut -d " " -f 1
```

```
lsof -p 12345 | grep -i established | wc -l
```
