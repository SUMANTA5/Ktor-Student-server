package com.example.repo

import com.example.dao.StudentDao
import com.example.data.model.Student
import com.example.data.table.StudentTable
import com.example.repo.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class StudentRepo: StudentDao {

    override suspend fun insert(name: String, age: Int): Student? {
        var statement: InsertStatement<Number>? = null
         dbQuery {
            statement = StudentTable.insert { student->
                student[StudentTable.name] = name
                student[StudentTable.age] = age

            }
        }
        return statement?.resultedValues?.get(0)?.let { rowToStudent(it) }
    }

    override suspend fun getAllStudents(): List<Student> = dbQuery {
        StudentTable.selectAll().mapNotNull {
            rowToStudent(it)
        }
    }

    override suspend fun getStudentById(userId: Int): Student? =
        dbQuery {
            StudentTable.select {
                StudentTable.userId.eq(userId)
            }
                .map {
                    rowToStudent(it)
                }.singleOrNull()
        }

    override suspend fun deleteById(userId: Int): Int =
        dbQuery {
            StudentTable.deleteWhere {
                StudentTable.userId.eq(userId)
            }
        }

    override suspend fun update(userId: Int, name: String, age: Int): Int =
        dbQuery {
            StudentTable.update({StudentTable.userId.eq(userId)}){ student->
                student[StudentTable.name] = name
                student[StudentTable.age] = age
            }
        }

    private fun rowToStudent(row: ResultRow) : Student? {
        if (row == null)
            return null
        return Student(
            userId = row[StudentTable.userId],
            name = row[StudentTable.name],
            age = row[StudentTable.age]
        )
    }
}