import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Intervals {
    private static final Map<String, Integer> intervalWeightMap = new TreeMap<>();

    static {
        intervalWeightMap.put("m2", 1);
        intervalWeightMap.put("M2", 2);
        intervalWeightMap.put("m3", 3);
        intervalWeightMap.put("M3", 4);
        intervalWeightMap.put("P4", 5);
        intervalWeightMap.put("P5", 7);
        intervalWeightMap.put("m6", 8);
        intervalWeightMap.put("M6", 9);
        intervalWeightMap.put("m7", 10);
        intervalWeightMap.put("M7", 11);
        intervalWeightMap.put("P8", 12);
    }
    private static final Character[] whiteOctave = {'C','D','E','F','G','A','B'};
    private static final Character[] fullOctave = {'C',0,'D',0,'E','F',0,'G',0,'A',0,'B'};
    private static final char SHARP = '#';
    private static final char FLAT = 'b';

    private static boolean asc;
    private static int weigh;
    private static char[] startNote;

    public static String intervalConstruction(String[] args) throws Exception {
        weigh = 0;
        asc = true;
        int length;
        StringBuilder result;

        if (args.length>3 || args.length<2) throw new Exception("Illegal number of elements in input array.");

        String element = args[0].trim();
        if (!intervalWeightMap.containsKey(element)) throw new Exception("Unknown interval name.");
        weigh = intervalWeightMap.get(element);
        length = Integer.parseInt(element.substring(1));

        startNote = args[1].trim().toCharArray();
        noteValidation(startNote[0]);

        if (args.length==3) isAsc(args[2]);

//       wright start note
        result = new StringBuilder().append(startNote).append(" ");

        if (startNote.length > 1) weigh += weighCorrector(startNote);

//        2. find end white note (whiteOctave, length)
            int endNoteIndex = asc
                    ? ArrayUtils.indexOf(whiteOctave, startNote[0])+length-1 //ASC
                    : ArrayUtils.indexOf(whiteOctave, startNote[0])-length+1;//DSC

            if (endNoteIndex >= whiteOctave.length) endNoteIndex -= whiteOctave.length;//ASC
            if (endNoteIndex < 0) endNoteIndex += whiteOctave.length;                  //DSC
            char endWhiteNote = whiteOctave[endNoteIndex];

//          wright end note
            result.append(endWhiteNote);

            weigh -= lengthCalculation(startNote[0], endWhiteNote, fullOctave);
//        3. check weight & add #/b/##/bb to result if necessary (fullOctave, weight)
            while (weigh>0) {
                if (asc) result.append(SHARP);//ASC
                else result.append(FLAT);             //DSC
                weigh--;
            }
            while (weigh<0){
                if (asc) result.append(FLAT);//ASC
                else result.append(SHARP);           //DSC
                weigh++;
            }
        return result.toString();
    }

    public static String intervalIdentification(String[] args) throws Exception {
        weigh = 0;
        asc = true;
        startNote = args[0].trim().toCharArray();
        char[] endNote = args[1].trim().toCharArray();

        noteValidation(startNote[0], endNote[0]);
        if (args.length==3) isAsc(args[2]);

        if (startNote.length > 1) weigh -= weighCorrector(startNote);
        if (endNote.length > 1) weigh += weighCorrector(endNote);
        weigh += lengthCalculation(startNote[0], endNote[0], fullOctave);

        if (intervalWeightMap.containsValue(weigh)) {
            for (Map.Entry<String, Integer> entry: intervalWeightMap.entrySet()) {
                if (entry.getValue()==weigh) {
                    return entry.getKey();
                }
            }
        }
        throw new Exception("Cannot identify the interval with weight "+weigh);
    }

    private static void isAsc(String element) throws Exception {
            if (!element.matches("asc|dsc")) throw new Exception("Unknown accidentals name.");
            if (element.equals("asc")) asc = true;
            else asc = false;
    }

    private static int lengthCalculation(char startNote, char endNote, Character[] octave){
        int length = asc
                ? ArrayUtils.indexOf(octave, endNote)-ArrayUtils.indexOf(octave, startNote)  //ASC
                : -ArrayUtils.indexOf(octave, endNote)+ArrayUtils.indexOf(octave, startNote);//DSC
        if (length < 0) length += octave.length;
        return length;
    }

    private static int weighCorrector(char[] startNote){
        int correction =0;
            for (int i = 1; i< startNote.length; i++) {
                if (startNote[i]==SHARP){
                    if (asc) correction++;//ASC
                    else correction--;       //DSC
                } else if (startNote[i]==FLAT){
                    if (asc) correction--;//ASC
                    else correction++;       //DSC
                }
            }
            return correction;
    }

    private static void noteValidation(char ... note) throws Exception {
        for (char n : note) {
            if (Arrays.stream(whiteOctave).noneMatch(ch -> ch==n)) throw new Exception("Unknown note name.");
        }
    }

}
