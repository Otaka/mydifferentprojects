package com.nes.processor.commands;

import com.nes.processor.AluUtils;
import com.nes.processor.commands.impl.*;
import com.nes.processor.memory_addressing.*;

/**
 * @author Dmitry
 */
public class CommandsDefinitionArray {

    private final CommandDefinition[] commandsDefinitions = new CommandDefinition[256];

    public CommandsDefinitionArray() {
        initCommands();
    }

    private void initCommands() {
        AbstractMemoryAdressing ABS = new ABS();
        AbstractMemoryAdressing ABS16 = new ABS16();
        AbstractMemoryAdressing ABS_X = new ABS_X();
        AbstractMemoryAdressing ABS_Y = new ABS_Y();
        AbstractMemoryAdressing IMM = new IMMEDIATE();
        AbstractMemoryAdressing IND_X = new IND_X();
        AbstractMemoryAdressing IND_Y = new IND_Y();
        AbstractMemoryAdressing ZP = new ZP();
        AbstractMemoryAdressing ZP_X = new ZP_X();
        AbstractMemoryAdressing ZP_Y = new ZP_Y();
        AbstractMemoryAdressing IMPLIED = new IMPLISIT();
        AbstractMemoryAdressing REL = new REL();
        AbstractMemoryAdressing INDIRECT = new INDIRECT();
        AbstractMemoryAdressing ACC = new ACC();
        AbstractMemoryAdressing X = new X();
        AbstractMemoryAdressing Y = new Y();

        addCommand(Commands.ADC_ABS, new Adc(), ABS);
        addCommand(Commands.ADC_ABS_X, new Adc(), ABS_X);
        addCommand(Commands.ADC_ABS_Y, new Adc(), ABS_Y);
        addCommand(Commands.ADC_IMM, new Adc(), IMM);
        addCommand(Commands.ADC_IND_X, new Adc(), IND_X);
        addCommand(Commands.ADC_IND_Y, new Adc(), IND_Y);
        addCommand(Commands.ADC_ZP, new Adc(), ZP);
        addCommand(Commands.ADC_ZP_X, new Adc(), ZP_X);

        addCommand(Commands.AND_ABS, new And(), ABS);
        addCommand(Commands.AND_ABS_X, new And(), ABS_X);
        addCommand(Commands.AND_ABS_Y, new And(), ABS_Y);
        addCommand(Commands.AND_IMM, new And(), IMM);
        addCommand(Commands.AND_IND_X, new And(), IND_X);
        addCommand(Commands.AND_IND_Y, new And(), IND_Y);
        addCommand(Commands.AND_ZP, new And(), ZP);
        addCommand(Commands.AND_ZP_X, new And(), ZP_X);

        addCommand(Commands.ASL_ABS, new Asl(), ABS);
        addCommand(Commands.ASL_ABS_X, new Asl(), ABS_X);
        addCommand(Commands.ASL_ACC, new Asl(), ACC);
        addCommand(Commands.ASL_ZP, new Asl(), ZP);
        addCommand(Commands.ASL_ZP_X, new Asl(), ZP_X);

        addCommand(Commands.BCC_REL, new Bcc(), REL);
        addCommand(Commands.BEQ_REL, new Beq(), REL);
        addCommand(Commands.BCS_REL, new Bcs(), REL);

        addCommand(Commands.BIT_ABS, new Bit(), ABS);
        addCommand(Commands.BIT_ZP, new Bit(), ZP);
        addCommand(Commands.BRK, new Brk(), IMPLIED);

        addCommand(Commands.BMI_REL, new Bmi(), REL);
        addCommand(Commands.BNE_REL, new Bne(), REL);
        addCommand(Commands.BPL_REL, new Bpl(), REL);

        addCommand(Commands.BVC_REL, new Bvc(), REL);
        addCommand(Commands.BVS_REL, new Bvs(), REL);

        addCommand(Commands.CLC, new Clc(), IMPLIED);
        addCommand(Commands.CLD, new Cld(), IMPLIED);
        addCommand(Commands.CLI, new Cli(), IMPLIED);
        addCommand(Commands.CLV, new Clv(), IMPLIED);

        addCommand(Commands.CMP_ABS, new Cmp(), ABS);
        addCommand(Commands.CMP_ABS_X, new Cmp(), ABS_X);
        addCommand(Commands.CMP_ABS_Y, new Cmp(), ABS_Y);
        addCommand(Commands.CMP_IMM, new Cmp(), IMM);
        addCommand(Commands.CMP_IND_X, new Cmp(), IND_X);
        addCommand(Commands.CMP_IND_Y, new Cmp(), IND_Y);
        addCommand(Commands.CMP_ZP, new Cmp(), ZP);
        addCommand(Commands.CMP_ZP_X, new Cmp(), ZP_X);

        addCommand(Commands.CPX_ABS, new Cpx(), ABS);
        addCommand(Commands.CPX_IMM, new Cpx(), IMM);
        addCommand(Commands.CPX_ZP, new Cpx(), ZP);

        addCommand(Commands.CPY_ABS, new Cpy(), ABS);
        addCommand(Commands.CPY_IMM, new Cpy(), IMM);
        addCommand(Commands.CPY_ZP, new Cpy(), ZP);

        addCommand(Commands.DEC_ABS, new Dec(), ABS);
        addCommand(Commands.DEC_ABS_X, new Dec(), ABS_X);
        addCommand(Commands.DEC_ZP, new Dec(), ZP);
        addCommand(Commands.DEC_ZP_X, new Dec(), ZP_X);

        addCommand(Commands.DEX, new Dec(), X);
        addCommand(Commands.DEY, new Dec(), Y);

        addCommand(Commands.EOR_ABS, new Eor(), ABS);
        addCommand(Commands.EOR_ABS_X, new Eor(), ABS_X);
        addCommand(Commands.EOR_ABS_Y, new Eor(), ABS_Y);
        addCommand(Commands.EOR_IMM, new Eor(), IMM);
        addCommand(Commands.EOR_IND_X, new Eor(), IND_X);
        addCommand(Commands.EOR_IND_Y, new Eor(), IND_Y);
        addCommand(Commands.EOR_ZP, new Eor(), ZP);
        addCommand(Commands.EOR_ZP_X, new Eor(), ZP_X);

        addCommand(Commands.INC_ABS, new Inc(), ABS);
        addCommand(Commands.INC_ABS_X, new Inc(), ABS_X);
        addCommand(Commands.INC_ZP, new Inc(), ZP);
        addCommand(Commands.INC_ZP_X, new Inc(), ZP_X);

        addCommand(Commands.INX, new Inc(), X);
        addCommand(Commands.INY, new Inc(), Y);

        addCommand(Commands.JMP_ABS16, new JmpAbsolute(), ABS16);
        addCommand(Commands.JMP_IND, new JmpIndirect(), INDIRECT);

        addCommand(Commands.JSR_ABS16, new Jsr(), ABS16);

        addCommand(Commands.LDA_ABS, new Lda(), ABS);
        addCommand(Commands.LDA_ABS_X, new Lda(), ABS_X);
        addCommand(Commands.LDA_ABS_Y, new Lda(), ABS_Y);
        addCommand(Commands.LDA_IMM, new Lda(), IMM);
        addCommand(Commands.LDA_IND_X, new Lda(), IND_X);
        addCommand(Commands.LDA_IND_Y, new Lda(), IND_Y);
        addCommand(Commands.LDA_ZP, new Lda(), ZP);
        addCommand(Commands.LDA_ZP_X, new Lda(), ZP_X);

        addCommand(Commands.LDX_ABS, new Ldx(), ABS);
        addCommand(Commands.LDX_ABS_Y, new Ldx(), ABS_Y);
        addCommand(Commands.LDX_IMM, new Ldx(), IMM);
        addCommand(Commands.LDX_ZP, new Ldx(), ZP);
        addCommand(Commands.LDX_ZP_Y, new Ldx(), ZP_Y);

        addCommand(Commands.LDY_ABS, new Ldy(), ABS);
        addCommand(Commands.LDY_ABS_X, new Ldy(), ABS_X);
        addCommand(Commands.LDY_IMM, new Ldy(), IMM);
        addCommand(Commands.LDY_ZP, new Ldy(), ZP);
        addCommand(Commands.LDY_ZP_X, new Ldy(), ZP_X);

        addCommand(Commands.LSR_ABS, new Lsr(), ABS);
        addCommand(Commands.LSR_ABS_X, new Lsr(), ABS_X);
        addCommand(Commands.LSR_ACC, new Lsr(), ACC);
        addCommand(Commands.LSR_ZP, new Lsr(), ZP);
        addCommand(Commands.LSR_ZP_X, new Lsr(), ZP_X);

        addCommand(Commands.NOP, new Nop(), IMPLIED);

        addCommand(Commands.ORA_ABS, new Ora(), ABS);
        addCommand(Commands.ORA_ABS_X, new Ora(), ABS_X);
        addCommand(Commands.ORA_ABS_Y, new Ora(), ABS_Y);
        addCommand(Commands.ORA_IMM, new Ora(), IMM);
        addCommand(Commands.ORA_IND_X, new Ora(), IND_X);
        addCommand(Commands.ORA_IND_Y, new Ora(), IND_Y);
        addCommand(Commands.ORA_ZP, new Ora(), ZP);
        addCommand(Commands.ORA_ZP_X, new Ora(), ZP_X);

        addCommand(Commands.PHA, new Pha(), IMPLIED);

        addCommand(Commands.PHP, new Php(), IMPLIED);
        addCommand(Commands.PLA, new Pla(), IMPLIED);
        addCommand(Commands.PLP, new Plp(), IMPLIED);

        addCommand(Commands.ROL_ABS, new Rol(), ABS);
        addCommand(Commands.ROL_ABS_X, new Rol(), ABS_X);
        addCommand(Commands.ROL_ACC, new Rol(), ACC);
        addCommand(Commands.ROL_ZP, new Rol(), ZP);
        addCommand(Commands.ROL_ZP_X, new Rol(), ZP_X);

        addCommand(Commands.ROR_ABS, new Ror(), ABS);
        addCommand(Commands.ROR_ABS_X, new Ror(), ABS_X);
        addCommand(Commands.ROR_ACC, new Ror(), ACC);
        addCommand(Commands.ROR_ZP, new Ror(), ZP);
        addCommand(Commands.ROR_ZP_X, new Ror(), ZP_X);

        addCommand(Commands.RTI, new Rti(), IMPLIED);
        addCommand(Commands.RTS, new Rts(), IMPLIED);

        addCommand(Commands.SBC_ABS, new Sbc(), ABS);
        addCommand(Commands.SBC_ABS_X, new Sbc(), ABS_X);
        addCommand(Commands.SBC_ABS_Y, new Sbc(), ABS_Y);
        addCommand(Commands.SBC_IMM, new Sbc(), IMM);
        addCommand(Commands.SBC_IND_X, new Sbc(), IND_X);
        addCommand(Commands.SBC_IND_Y, new Sbc(), IND_Y);
        addCommand(Commands.SBC_ZP, new Sbc(), ZP);
        addCommand(Commands.SBC_ZP_X, new Sbc(), ZP_X);

        addCommand(Commands.SEC, new Sec(), IMPLIED);
        addCommand(Commands.SED, new Sed(), IMPLIED);
        addCommand(Commands.SEI, new Sei(), IMPLIED);

        addCommand(Commands.STA_ABS, new Sta(), ABS);
        addCommand(Commands.STA_ABS_X, new Sta(), ABS_X);
        addCommand(Commands.STA_ABS_Y, new Sta(), ABS_Y);
        addCommand(Commands.STA_IND_X, new Sta(), IND_X);
        addCommand(Commands.STA_IND_Y, new Sta(), IND_Y);
        addCommand(Commands.STA_ZP, new Sta(), ZP);
        addCommand(Commands.STA_ZP_X, new Sta(), ZP_X);

        addCommand(Commands.STX_ABS, new Stx(), ABS);
        addCommand(Commands.STX_ZP, new Stx(), ZP);
        addCommand(Commands.STX_ZP_Y, new Stx(), ZP_Y);

        addCommand(Commands.STY_ABS, new Sty(), ABS);
        addCommand(Commands.STY_ZP, new Sty(), ZP);
        addCommand(Commands.STY_ZP_X, new Sty(), ZP_X);

        addCommand(Commands.TAX, new Tax(), IMPLIED);
        addCommand(Commands.TAY, new Tay(), IMPLIED);
        addCommand(Commands.TSX, new Tsx(), IMPLIED);
        addCommand(Commands.TXA, new Txa(), IMPLIED);
        addCommand(Commands.TXS, new Txs(), IMPLIED);
        addCommand(Commands.TYA, new Tya(), IMPLIED);
        
        //Unofficial
        addCommand(Commands.NOP_ZP, new Nop(), ZP, true);
        addCommand(Commands.NOP_ZP$1, new Nop(), ZP, true);
        addCommand(Commands.NOP_ZP$2, new Nop(), ZP, true);
        addCommand(Commands.NOP_ABS, new Nop(), ABS, true);
        addCommand(Commands.NOP_ZP_X, new Nop(), ZP_X, true);
        addCommand(Commands.NOP_ZP_X$1, new Nop(), ZP_X, true);
        addCommand(Commands.NOP_ZP_X$2, new Nop(), ZP_X, true);
        addCommand(Commands.NOP_ZP_X$3, new Nop(), ZP_X, true);
        addCommand(Commands.NOP_ZP_X$4, new Nop(), ZP_X, true);
        addCommand(Commands.NOP_ZP_X$5, new Nop(), ZP_X, true);
        addCommand(Commands.NOP$1, new Nop(), IMPLIED, true);
        addCommand(Commands.NOP$2, new Nop(), IMPLIED, true);
        addCommand(Commands.NOP$3, new Nop(), IMPLIED, true);
        addCommand(Commands.NOP$4, new Nop(), IMPLIED, true);
        addCommand(Commands.NOP$5, new Nop(), IMPLIED, true);
        addCommand(Commands.NOP$6, new Nop(), IMPLIED, true);
        addCommand(Commands.NOP_IMM, new Nop(), IMM, true);
        addCommand(Commands.NOP_IMM$1, new Nop(), IMM, true);
        addCommand(Commands.NOP_IMM$2, new Nop(), IMM, true);
        addCommand(Commands.NOP_ABS_X$1, new Nop(), new ABS_X(), true);
        addCommand(Commands.NOP_ABS_X$2, new Nop(), new ABS_X(), true);
        addCommand(Commands.NOP_ABS_X$3, new Nop(), new ABS_X(), true);
        addCommand(Commands.NOP_ABS_X$4, new Nop(), new ABS_X(), true);
        addCommand(Commands.NOP_ABS_X$5, new Nop(), new ABS_X(), true);
        addCommand(Commands.NOP_ABS_X$6, new Nop(), new ABS_X(), true);


        addCommand(Commands.LAX_ZP, new Lax(), ZP, true);
        addCommand(Commands.LAX_ZP_Y, new Lax(), ZP_Y, true);
        addCommand(Commands.LAX_ABS, new Lax(), ABS, true);
        addCommand(Commands.LAX_ABS_Y, new Lax(), ABS_Y, true);
        addCommand(Commands.LAX_IND_X, new Lax(), IND_X, true);
        addCommand(Commands.LAX_IND_Y, new Lax(), IND_Y, true);

        addCommand(Commands.SAX_ABS, new Sax(), ABS, true);
        addCommand(Commands.SAX_IND_X, new Sax(), IND_X, true);
        addCommand(Commands.SAX_ZP, new Sax(), ZP, true);
        addCommand(Commands.SAX_ZP_Y, new Sax(), ZP_Y, true);
        addCommand(Commands.SBC_IMM$1, new Sbc(), IMM, true);
        addCommand(Commands.DCP_IND_X, new Dcp(), IND_X, true);
        addCommand(Commands.DCP_IND_Y, new Dcp(), IND_Y, true);
        addCommand(Commands.DCP_ZP, new Dcp(), ZP, true);
        addCommand(Commands.DCP_ZP_X, new Dcp(), ZP_X, true);
        addCommand(Commands.DCP_ABS, new Dcp(), ABS, true);
        addCommand(Commands.DCP_ABS_X, new Dcp(), ABS_X, true);
        addCommand(Commands.DCP_ABS_Y, new Dcp(), ABS_Y, true);
        
        addCommand(Commands.ISB_ABS, new Isb(), ABS, true);
        addCommand(Commands.ISB_ABS_X, new Isb(), ABS_X, true);
        addCommand(Commands.ISB_ABS_Y, new Isb(), ABS_Y, true);
        addCommand(Commands.ISB_IND_X, new Isb(), IND_X, true);
        addCommand(Commands.ISB_IND_Y, new Isb(), IND_Y, true);
        addCommand(Commands.ISB_ZP, new Isb(), ZP, true);
        addCommand(Commands.ISB_ZP_X, new Isb(), ZP_X, true);
        
        addCommand(Commands.SLO_ABS, new Slo(), ABS, true);
        addCommand(Commands.SLO_ABS_X, new Slo(), ABS_X, true);
        addCommand(Commands.SLO_ABS_Y, new Slo(), ABS_Y, true);
        addCommand(Commands.SLO_IND_X, new Slo(), IND_X, true);
        addCommand(Commands.SLO_IND_Y, new Slo(), IND_Y, true);
        addCommand(Commands.SLO_ZP, new Slo(), ZP, true);
        addCommand(Commands.SLO_ZP_X, new Slo(), ZP_X, true);
       
        addCommand(Commands.RLA_ABS, new Rla(), ABS, true);
        addCommand(Commands.RLA_ABS_X, new Rla(), ABS_X, true);
        addCommand(Commands.RLA_ABS_Y, new Rla(), ABS_Y, true);
        addCommand(Commands.RLA_IND_X, new Rla(), IND_X, true);
        addCommand(Commands.RLA_IND_Y, new Rla(), IND_Y, true);
        addCommand(Commands.RLA_ZP, new Rla(), ZP, true);
        addCommand(Commands.RLA_ZP_X, new Rla(), ZP_X, true);
        
        addCommand(Commands.SRE_ABS, new Sre(), ABS, true);
        addCommand(Commands.SRE_ABS_X, new Sre(), ABS_X, true);
        addCommand(Commands.SRE_ABS_Y, new Sre(), ABS_Y, true);
        addCommand(Commands.SRE_IND_X, new Sre(), IND_X, true);
        addCommand(Commands.SRE_IND_Y, new Sre(), IND_Y, true);
        addCommand(Commands.SRE_ZP, new Sre(), ZP, true);
        addCommand(Commands.SRE_ZP_X, new Sre(), ZP_X, true);
        
        addCommand(Commands.RRA_ABS, new Rra(), ABS, true);
        addCommand(Commands.RRA_ABS_X, new Rra(), ABS_X, true);
        addCommand(Commands.RRA_ABS_Y, new Rra(), ABS_Y, true);
        addCommand(Commands.RRA_IND_X, new Rra(), IND_X, true);
        addCommand(Commands.RRA_IND_Y, new Rra(), IND_Y, true);
        addCommand(Commands.RRA_ZP, new Rra(), ZP, true);
        addCommand(Commands.RRA_ZP_X, new Rra(), ZP_X, true);
    }

    public CommandDefinition getCommandDefinition(byte commandByte) {
        int commandInt = AluUtils.unsignedByte(commandByte);
        CommandDefinition command = commandsDefinitions[commandInt];
        if (command == null) {
            throw new RuntimeException(String.format("Unknown command %s", Integer.toHexString(commandInt)));
        }

        return command;
    }

    public CommandDefinition[] getCommandsDefinitions() {
        return commandsDefinitions;
    }

    private void addCommand(byte command, AbstractCommand abstractCommand, AbstractMemoryAdressing abstractMemoryAdressing) {
        addCommand(new CommandDefinition(command, abstractCommand, abstractMemoryAdressing, false));
    }

    private void addCommand(byte command, AbstractCommand abstractCommand, AbstractMemoryAdressing abstractMemoryAdressing, boolean unofficial) {
        addCommand(new CommandDefinition(command, abstractCommand, abstractMemoryAdressing, unofficial));
    }

    private void addCommand(CommandDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Cannot add null command to commandsDefinitionArray");
        }
        int command = AluUtils.unsignedByte(definition.getCode());

        if (commandsDefinitions[command] != null) {
            throw new RuntimeException(String.format("Command %d already exist %s - %s",
                    command,
                    definition.getCommand().getClass().getSimpleName(),
                    commandsDefinitions[command].getCommand().getClass().getSimpleName()));
        }

        commandsDefinitions[command] = definition;
    }
}
