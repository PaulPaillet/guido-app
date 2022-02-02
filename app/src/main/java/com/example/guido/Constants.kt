package com.example.guido

object Constants{

    const val USER_NAME: String = "user name"
    const val TOTAL_QUESTIONS: String = "total_question"
    const val CORRECT_ANSWERS: String = "correct_answers"
    fun getQuestions() :ArrayList<Question> {
        val questionsList = ArrayList<Question>()

        val que1 = Question(1,
            "Quelle est cette note ?",
            R.drawable.ic_do,
            "Do",
            "Re",
            "Mi",
            "Fa",
            1)

        val que2 = Question(2,
            "Quelle est cette note ?",
            R.drawable.ic_re,
            "Si",
            "Re",
            "La",
            "Sol",
            2)

        val que3 = Question(3,
            "Quelle est cette note ?",
            R.drawable.ic_fa,
            "Sol",
            "Mi",
            "Fa",
            "Do",
            3)

        val que4 = Question(4,
            "Quelle est cette note ?",
            R.drawable.ic_sol,
            "Sol",
            "Mi",
            "Do",
            "Fa",
            1)

        questionsList.add(que1)
        questionsList.add(que2)
        questionsList.add(que3)
        questionsList.add(que4)

        return questionsList
    }
}