package com.goodchoice.infra.persistence

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive
import org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization

fun runAfterTxCommit(action: () -> Unit) {
    if (isActualTransactionActive()) {
        registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                action()
            }
        })

    } else {
        action()
    }
}
