/* 
 0xC4
*/
module circuit_0xC4
(
 a,
 b,
 c,
 out
 );

   input a,b,c;
   output out;

   always@(a,b,c)
	 begin
		case({a,b,c})
		  3'b000: {out} = 1'b1;
		  3'b001: {out} = 1'b1;
		  3'b010: {out} = 1'b0;
		  3'b011: {out} = 1'b0;
		  3'b100: {out} = 1'b0;
		  3'b101: {out} = 1'b1;
		  3'b110: {out} = 1'b0;
		  3'b111: {out} = 1'b0;
		endcase
	 end
 
endmodule // circuit_0xC4

