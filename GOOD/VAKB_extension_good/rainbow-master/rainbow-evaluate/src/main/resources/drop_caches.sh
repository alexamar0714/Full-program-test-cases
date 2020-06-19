#!/bin/bash

for ((i=0;i<=4;i++))
do
  ssh <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bfcdd0d0cbffcfcddacccbd08f">[email protected]</a>$i "echo 3 > /proc/sys/vm/drop_caches"
  ssh <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a4d6cbcbd0e4d4d6c1d7d0cb94">[email protected]</a>$i sync
done