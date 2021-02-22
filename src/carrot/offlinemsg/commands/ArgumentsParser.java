package carrot.offlinemsg.commands;

import java.util.ArrayList;
import java.util.Arrays;

public final class ArgumentsParser {
    /** Returns true if the index is too big, meaning the array is too small. True == Out of range
     * @param array the args array
     * @param indexableLength The length - 1, aka the max length required to index without worry... sort of
     * @return If index is out of range
     */
    public static boolean ArgsTooSmall(Object[] array, int indexableLength){
        if (array == null){
            return false;
        }
        return array.length <= indexableLength;
    }
    public static ParsedValue<String> ParseString(String[] args, int index) {
        if (ArgsTooSmall(args, index)) {
            return new ParsedValue<String>("ERR_STR", true);
        }
        return new ParsedValue<String>(args[index], false);
    }

    public static ParsedValue<Integer> ParseInteger(String[] args, int index){
        try{
            if (ArgsTooSmall(args, index)) {
                return new ParsedValue<Integer>(0, true);
            }
            return new ParsedValue<Integer>(Integer.parseInt(args[index]), false);
        }
        catch (Exception e){
            return new ParsedValue<Integer>(0, true);
        }
    }
    public static ParsedValue<Double> ParseDouble(String[] args, int index){
        try{
            if (ArgsTooSmall(args, index)) {
                return new ParsedValue<Double>(0.0d, true);
            }
            return new ParsedValue<Double>(Double.parseDouble(args[index]), false);
        }
        catch (Exception e){
            return new ParsedValue<Double>(0.0d, true);
        }
    }

    public static String GetCommand(String[] fullArgs){
        if (ArgsTooSmall(fullArgs, 0)){
            return "intern_cmd_broken_rip_xd";
        }
        return fullArgs[0];
    }

    public static ArrayList<String> GetCommandArgs(String[] fullArgs){
        if (fullArgs == null || fullArgs.length <= 0){
            return new ArrayList<String>();
        }
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(fullArgs));
        args.remove(0);
        return args;
    }

    public static String[] ToArray(ArrayList<String> arr){
        return arr.toArray(new String[0]);
    }
}
