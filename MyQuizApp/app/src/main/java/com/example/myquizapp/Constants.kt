package com.example.myquizapp

object Constants {

    const val USER_NAME : String = "user_name"
    const val TOTAL_QUESTIONS : String = "total_questions"
    const val CORRECT_ANSWERS : String = "correct_answers"

    fun getQuestions(): ArrayList<Question>{
        val questionsList = ArrayList<Question>()

        val question = "What country does this flag belong to?"

        val que1 = Question(
            1,
            question,
            R.drawable.ic_flag_of_argentina,
            "Argentina",
            "Australia",
            "Armenia",
            "Austria",
            1)

        questionsList.add(que1)

        val que2 = Question(
            2,
            question,
            R.drawable.ic_flag_of_australia,
            "Argentina",
            "Australia",
            "Armenia",
            "Austria",
            2)

        questionsList.add(que2)

        val que3 = Question(
            3,
            question,
            R.drawable.ic_flag_of_belgium,
            "Bahamas",
            "Australia",
            "Armenia",
            "Belgium",
            4)

        questionsList.add(que3)

        val que4 = Question(
            4,
            question,
            R.drawable.ic_flag_of_brazil,
            "Belgium",
            "Bahamas",
            "Brazil",
            "Austria",
            3)

        questionsList.add(que4)

        val que5 = Question(
            5,
            question,
            R.drawable.ic_flag_of_denmark,
            "Denmark",
            "Finland",
            "Sweden",
            "Netherlands",
            1)

        questionsList.add(que5)

        val que6 = Question(
            6,
            question,
            R.drawable.ic_flag_of_fiji,
            "Gabon",
            "Barbados",
            "Belize",
            "Fiji",
            4)

        questionsList.add(que6)

        val que7 = Question(
            7,
            question,
            R.drawable.ic_flag_of_germany,
            "Belgium",
            "Germany",
            "Hungary",
            "Poland",
            2)

        questionsList.add(que7)

        val que8 = Question(
            8,
            question,
            R.drawable.ic_flag_of_india,
            "India",
            "Vietnam",
            "China",
            "Japan",
            1)

        questionsList.add(que8)

        val que9 = Question(
            9,
            question,
            R.drawable.ic_flag_of_kuwait,
            "Uruguay",
            "Monaco",
            "Kuwait",
            "Egypt",
            3)

        questionsList.add(que9)

        val que10 = Question(
            10,
            question,
            R.drawable.ic_flag_of_new_zealand,
            "Philippines",
            "New Zealand",
            "Australia",
            "Austria",
            2)

        questionsList.add(que10)

        return questionsList
    }
}