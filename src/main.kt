import java.io.File

val files = mapOf(
    "src/a_in.txt" to "src/a_out.txt",
    "src/b_in.txt" to "src/b_out.txt",
    "src/c_in.txt" to "src/c_out.txt",
    "src/d_in.txt" to "src/d_out.txt",
    "src/e_in.txt" to "src/e_out.txt",
    "src/f_in.txt" to "src/f_out.txt"
)

fun main() {
    files.forEach { file ->
        val fileLines = readFileAsLinesUsingUseLines(file.key)
        //val days = fileLines[0][2]
        val librariesNumber = fileLines[0][1]
        val booksScore = fileLines[1]
        val librariesList = mutableListOf<Library>()
        buildLibraries(fileLines, librariesNumber, librariesList)

        // sort libraries by sign up
        val sortedLibraries = librariesList.sortedBy { it.signUpTime }

        // sort books by rating
        sortedLibraries.map { library ->
            library.books = library.books.sortedByDescending { booksScore[it] }
            return@map library
        }

        // remove produced books
        val producedBooks = mutableListOf<Int>()
        sortedLibraries.map { library ->
            val bookForProduction = mutableListOf<Int>()
            library.books.forEach {
                if(!producedBooks.contains(it)){
                    bookForProduction.add(it)
                    producedBooks.add(it)
                }
            }
            library.books = bookForProduction
            return@map library
        }
        writeFile(sortedLibraries, file.value)
    }
}
private fun buildLibraries(
    fileLines: List<List<Int>>,
    librariesNumber: Int,
    librariesList: MutableList<Library>
) {
    var libraryId = 0
    fileLines.subList(2, 2 + librariesNumber * 2).forEachIndexed { index, item ->
        if (index % 2 == 0) {
            librariesList.add(
                Library(
                    id = libraryId++,
                    signUpTime = item[1],
                    booksPerDay = item[2]
                )
            )
        } else {
            librariesList[libraryId - 1].books = item
        }
    }
}

fun writeFile(libraries: List<Library>, fileName: String) {
    var result = "${libraries.count { it.books.isNotEmpty() }}\n"
    libraries.forEach { library ->
        if(library.books.isNotEmpty()){
            result += "${library.id} ${library.books.size}\n"
            library.books.forEach {
                result += "$it "
            }
            result.trimEnd()
            result += "\n"
        }
    }
    File(fileName).apply {
        createNewFile()
        writeText(result)
        println("File $fileName created.")
    }
}

fun readFileAsLinesUsingUseLines(fileName: String): List<List<Int>> =
    File(fileName).useLines {
        it.toList().mapNotNull { line ->
            if (line != "") {
                line.split(" ").map { value ->
                    value.toInt()
                }
            } else {
                null
            }
        }
    }


data class Library(
    val id: Int,
    val signUpTime: Int,
    val booksPerDay: Int
) {
    var books: List<Int> = emptyList()

    override fun toString(): String {
        return "Library(id: $id, signUpTime: $signUpTime, booksPerDay: $booksPerDay, books: $books)"
    }
}