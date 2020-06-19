#!/bin/bash

for ((i=0;i<=4;i++))
do
  ssh <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2c5e4343586c5c5e495f58431c">[email protected]</a>$i "echo 3 > /proc/sys/vm/drop_caches"
  ssh <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="75071a1a013505071006011a45">[email protected]</a>$i sync
done