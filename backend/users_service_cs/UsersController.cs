using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

namespace MyApp.Namespace
{
    [ApiController]
    [Route("users")]
    public class UsersController : ControllerBase
    {
        private readonly UsersDbContext db;

        public UsersController(UsersDbContext db)
        {
            this.db = db;
        }

        [HttpPost]
        [Route("login")]
        public async Task<IResult> DoLogin(UserCredentials credentials)
        {
           var user = await db.Users.FirstOrDefaultAsync(x => x.Username == credentials.username);
           if(user == null || credentials.username != credentials.password) {
              return Results.BadRequest($"User '{credentials.username}' not found or provided bad credentials");
           }

           var currentTimestamp = DateTime.UtcNow;
           var jwtToken = new JwtSecurityToken(
               notBefore: currentTimestamp,
               expires: currentTimestamp.AddHours(1),
               claims: new List<Claim> { new Claim(ClaimTypes.Name, user.Id.ToString()) },
               signingCredentials: new SigningCredentials(
                   new SymmetricSecurityKey(Encoding.UTF8.GetBytes("mysecretkeythatislongenoughforthehashingalgorithmmysecretkeythan")),
                   SecurityAlgorithms.HmacSha512Signature));

           return Results.Ok(new Dictionary<string, string> {
               { "username", user.Username },
               { "jwt", new JwtSecurityTokenHandler().WriteToken(jwtToken) }
           });
        }
    }
}
