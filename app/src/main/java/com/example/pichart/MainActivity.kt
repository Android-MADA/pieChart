package com.example.pichart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pichart.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate


class MainActivity : AppCompatActivity() {
    private lateinit var customCircleBarView: CustomCircleBarView       //프로그래스바
    data class PieChartData(
        val title: String,
        val memo: String,
        val startHour: Int,
        val startMin: Int,
        val endHour: Int,
        val endMin: Int,
        val colorCode: String,
        val divisionNumber: Int
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate((layoutInflater))
        setContentView(binding.root)

        customCircleBarView = binding.progressbar
        // 원형 프로그레스 바 진행 상태 변경 (0부터 100까지)
        customCircleBarView.setProgress(36)

        //파이차트
        var chart = binding.chart
        val pieChartDataArray = arrayOf(        //임시 데이터
            PieChartData("제목1", "메모1", 0,0,1,0, "#486DA3",0),      //제목, 메모, 시작 시각, 시작 분, 끝 시각, 끝 분, 색깔 코드, 구분 숫자
            PieChartData("제목2", "메모2", 1,0,6,0, "#516773",1),
            PieChartData("제목3", "메모3", 9,0,12,0, "#FDA4B4",2),
            PieChartData("제목4", "메모4", 12,0,13,30, "#52B6C9",3),
            PieChartData("제목5", "메모5", 13,30,14,30, "#516773",4),
            PieChartData("제목6", "메모6", 14,30,16,30, "#52B6C9",5),
            PieChartData("제목7", "메모7", 16,30,18,0, "#FCE79A",6),
            PieChartData("제목8", "메모8", 20,0,22,0, "#486DA3",7),
            PieChartData("제목9", "메모9", 22,0,24,0, "#FCE79A",8)
        )
        val marker_ = YourMarkerView(this, R.layout.custom_label_layout,pieChartDataArray)
        val entries = ArrayList<PieEntry>()
        val colorsItems = ArrayList<Int>()

        var tmp = 0     //시작 시간

       for(data in pieChartDataArray) {
           val start = data.startHour.toString().toInt() * 60 + data.startMin.toString().toInt()
           val end = data.endHour.toString().toInt() * 60 + data.endMin.toString().toInt()
           if(tmp==start) {      //이전 일정과 사이에 빈틈이 없을때
               entries.add(PieEntry((end-start).toFloat(), data.divisionNumber.toString()))
               colorsItems.add(Color.parseColor(data.colorCode.toString()))
               tmp = end
           } else {
               val noScheduleDuration = start - tmp
               entries.add(PieEntry(noScheduleDuration.toFloat(), "999"))      // 스케줄 없는 시간
               colorsItems.add(Color.parseColor("#FFFFFF"))
               entries.add(PieEntry((end-start).toFloat(), data.divisionNumber.toString()))
               colorsItems.add(Color.parseColor(data.colorCode.toString()))
               tmp = end
           }
       }

        // 왼쪽 아래 설명 제거
        val legend = chart.legend
        legend.isEnabled = false
        chart.invalidate()

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.apply {
            colors = colorsItems
            setDrawValues(false) // 비율 숫자 없애기

        }

        val pieData = PieData(pieDataSet)



        chart.apply {
            data = pieData
            isRotationEnabled = false                               //드래그로 회전 x
            isDrawHoleEnabled = false                               //중간 홀 그리기 x
            setExtraOffsets(20f,20f,20f,20f)    //크기 조절
            setUsePercentValues(false)
            setEntryLabelColor(Color.BLACK)
            marker = marker_
            setDrawEntryLabels(false) //라벨 끄기
            //rotationAngle = 30f // 회전 각도, 굳이 필요 없을듯
            description.isEnabled = false   //라벨 끄기 (오른쪽아래 간단한 설명)
        }
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    val pieEntry = e as PieEntry
                    val label = pieEntry.label

                    if (label == "999") {
                        pieDataSet.selectionShift = 1f //하이라이트 크기
                    } else {
                        pieDataSet.selectionShift = 36f // 다른 라벨의 경우 선택 시 하이라이트 크기 설정
                    }
                }
            }
            override fun onNothingSelected() {
                // 아무 것도 선택되지 않았을 때의 동작을 구현합니다.
            }
        })




    }
}
