export KDU_COMPRESS=$1
$KDU_COMPRESS -i $2 -o $3 -rate 0.5 Clayers=1 Clevels=7 'Cprecincts={256,256},{256,256},{256,256},{128,128},{128,128},{64,64},{64,64},{32,32},{16,16}' 'Corder=RPCL' 'ORGgen_plt=yes' 'Cblk={32,32}' Cuse_sop=yes >/dev/null 2>&1
