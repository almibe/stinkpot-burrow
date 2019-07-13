/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.almibe.ligature.store

import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.StoreConfig
import jetbrains.exodus.env.Transaction
import org.almibe.ligature.*
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Stream
import kotlin.concurrent.withLock

internal object WriteLock {
    val lock = ReentrantLock()
}

internal class XodusDataset private constructor(private val name: String,
                                                private val environment: Environment): Dataset {
    private val sparqlRunner = SparqlRunner(environment)

    companion object {
        val suffixes = mapOf(
            "cntr" to "#cntr",
            "graphId" to "#gid",
            "idGraph" to "#idg",
            "nodeId" to "#nid",
            "idNode" to "#idn",
            "literalId" to "#lid",
            "idLiteral" to "#idl",
            "spo" to "#spo",
            "sop" to "#sop",
            "pos" to "#pos",
            "pso" to "#pso",
            "osp" to "#osp",
            "ops" to "#ops"
        )

        fun createOrOpen(name: String, environment: Environment): XodusDataset {
            environment.executeInTransaction { txn ->
                suffixes.values.forEach {  suffix ->
                    environment.openStore("$name$suffix", StoreConfig.WITHOUT_DUPLICATES, txn)
                }
            }
            return XodusDataset(name, environment)
        }

        fun delete(name: String, environment: Environment) {
            WriteLock.lock.withLock {
                environment.executeInExclusiveTransaction {  txn ->
                    suffixes.values.forEach { suffix ->
                        val storeName = "$name$suffix"
                        val store = environment.openStore(storeName, StoreConfig.USE_EXISTING, txn, false)
                        if (store != null) {
                            environment.removeStore(storeName, txn)
                        }
                    }
                }
            }
        }
    }

    override fun getDatasetName(): String = name

    override fun addStatements(statements: Collection<Quad>) {
        WriteLock.lock.withLock {
            environment.executeInExclusiveTransaction { txn ->
                statements.forEach { statement ->
                    val graphId = fetchOrCreateGraphId(statement.graph, txn)
                    val subjectId = fetchOrCreateSubjectId(statement.subject, txn)
                    val predicateId = fetchOrCreatePredicateId(statement.predicate, txn)
                    val objectId = fetchOrCreateObjectId(statement.`object`, txn)
                    insertStatement(graphId, subjectId, predicateId, objectId, txn)
                }
            }
        }
    }

    override fun executeSparql(sparql: String): Stream<List<SparqlResultField>> {
        return sparqlRunner.executeSparql(sparql)
    }

    override fun findAll(subject: Subject?, predicate: Predicate?, `object`: Object?, graph: Graph?): Stream<Quad> {
        return environment.computeInReadonlyTransaction {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    override fun removeStatements(statements: Collection<Quad>) {
        WriteLock.lock.withLock {
            environment.executeInExclusiveTransaction { txn ->
                for (statement in statements) {
                    val graphId = fetchGraphId(statement.graph, txn) ?: continue
                    val subjectId = fetchSubjectId(statement.subject, txn) ?: continue
                    val predicateId = fetchPredicateId(statement.predicate, txn) ?: continue
                    val objectId = fetchObjectId(statement.`object`, txn) ?: continue
                    removeStatement(graphId, subjectId, predicateId, objectId, txn)
                }
            }
        }
    }

    private fun fetchOrCreateGraphId(graph: Graph, txn: Transaction): Long {
        TODO()
    }

    private fun fetchOrCreateSubjectId(subject: Subject, txn: Transaction): Long {
        TODO()
    }

    private fun fetchOrCreatePredicateId(predicate: Predicate, txn: Transaction): Long {
        TODO()
    }

    private fun fetchOrCreateObjectId(`object`: Object, txn: Transaction): Long {
        TODO()
    }

    private fun fetchGraphId(graph: Graph, txn: Transaction): Long? {
        TODO()
    }

    private fun fetchSubjectId(subject: Subject, txn: Transaction): Long? {
        TODO()
    }

    private fun fetchPredicateId(predicate: Predicate, txn: Transaction): Long? {
        TODO()
    }

    private fun fetchObjectId(`object`: Object, txn: Transaction): Long? {
        TODO()
    }

    private fun insertStatement(graphId: Long, subjectId: Long, predicateId: Long, objectId: Long, txn: Transaction) {
        TODO()
    }

    private fun removeStatement(graphId: Long, subjectId: Long, predicateId: Long, objectId: Long, txn: Transaction) {
        TODO()
    }
}
