## rgw节点挂掉

```
root@node1:~# systemctl list-units --type=service|grep ceph  #看该节点当前运行的服务有哪些
	● ceph-d2ce33a4-e0b7-11ec-bb1e-461c91943ba5@node-exporter.node1.service         loaded failed failed  Ceph node-exporter.node1 for d2ce33a4-e0b7-11ec-bb1e-461c91943ba5            
	● ceph-d2ce33a4-e0b7-11ec-bb1e-461c91943ba5@rgw.rgw01.sztu.node1.scbiuo.service loaded failed failed  Ceph rgw.rgw01.sztu.node1.scbiuo for d2ce33a4-e0b7-11ec-bb1e-461c91943ba5 
	
root@node1:~# systemctl restart ceph-d2ce33a4-e0b7-11ec-bb1e461c91943ba5@rgw.rgw01.sztu.node1.scbiuo.service  #重启失败的服务

root@node1:~# ceph orch ps --daemon-type rgw  #查看rgw的节点运行是否正常
```

