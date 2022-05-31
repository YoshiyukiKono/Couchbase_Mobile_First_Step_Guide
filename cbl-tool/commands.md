```
cblite travel-sample.cblite2
```

```
ls -l --limit 5
```

```
quit
```

```
cblite info travel-sample.cblite2
```

```
cblite ls -l --limit 10 travel-sample.cblite2
```

```
query {"FROM":[{"COLLECTION":"_"}],"GROUP_BY":[[".state"]],"ORDER_BY":[[".num"]],"WHAT":[[".state"],["AS",["COUNT()",["."]],"num"]],"WHERE":["=",[".type"],"hotel"]}
```

```
select name from _ where type = 'hotel' order by name limit 10
```

```
select --explain state, COUNT(*) AS num from _ where type = 'hotel' group by state order by num
```
