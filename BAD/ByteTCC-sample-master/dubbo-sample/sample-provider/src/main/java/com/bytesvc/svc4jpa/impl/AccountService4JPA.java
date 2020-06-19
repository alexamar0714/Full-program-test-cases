package com.bytesvc.svc4jpa.impl;

import org.bytesoft.compensable.Compensable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytesvc.ServiceException;
import com.bytesvc.dao.IAccountDao;
import com.bytesvc.dao.model.Account;
import com.bytesvc.service.IAccountService;

@Service("accountService4JPA")
@Compensable(interfaceClass = IAccountService.class, confirmableKey = "accountServiceConfirm4JPA", cancellableKey = "accountServiceCancel4JPA")
public class AccountService4JPA implements IAccountService {

	@SuppressWarnings("restriction")
	@javax.annotation.Resource(name = "accountDao")
	private IAccountDao accountDao;

	@Transactional(rollbackFor = ServiceException.class)
	public void increaseAmount(String acctId, double amount) throws ServiceException {
		Account account = this.accountDao.findById(acctId);
		account.setFrozen(account.getFrozen() + amount); // 真实业务中, 请考虑设置乐观锁/悲观锁, 以便并发操作时导致数据不一致
		this.accountDao.update(account);
		System.out.printf("[jpa] exec increase: acct= %s, amount= %7.2f%n", acctId, amount);
	}

	@Transactional(rollbackFor = ServiceException.class)
	public void decreaseAmount(String acctId, double amount) throws ServiceException {
		Account account = this.accountDao.findById(acctId);
		account.setAmount(account.getAmount() - amount); // 真实业务中, 请考虑设置乐观锁/悲观锁, 以便并发操作时导致数据不一致
		account.setFrozen(account.getFrozen() + amount);
		this.accountDao.update(account);
		System.out.printf("[jpa] exec decrease: acct= %s, amount= %7.2f%n", acctId, amount);
		// throw new ServiceException("rollback");
	}

}
