
```
ps | grep sync_gateway | grep -v grep | cut -d " " -f 1
```

```
lsof -p 38723 | grep -i established | wc -l
```
