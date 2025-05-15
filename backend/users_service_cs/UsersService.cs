using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

public static class UsersService
{
  public class User
  {
    public User(int id, string username)
    {
     Id = id;
     Username = username;
    }

    public int Id { get; set; }
    public string Username { get; set; }
  }

  private class UserCredentials
  {
    public string? username { get; set; }
    public string? password { get; set; }
  }

  private static List<User> users = new List<User>() {
    new User(100, "admin"),
    new User(101, "User1"),
    new User(800, "Guest")
  };

    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Services.AddControllers();
        var app = builder.Build();

    app.MapPost("/users/login", (UserCredentials credentials) =>
    {
     var user = users.FirstOrDefault(x => x.Username == credentials.username);
     if(user == null) {
      return Results.BadRequest($"User '{credentials.username}' not found or provided bad credentials");
     }

     var currentTimestamp = DateTime.UtcNow;
     var jwtToken = new JwtSecurityToken(
      notBefore: currentTimestamp,
      expires: currentTimestamp.AddHours(1),
      claims: new List<Claim> { new Claim(ClaimTypes.Name, user.Id.ToString()) },
      signingCredentials: new SigningCredentials(
       new SymmetricSecurityKey(Encoding.UTF8.GetBytes("mysecretkeythatislongenoughforthehashingalgorithmmysecretkeythan")),
       SecurityAlgorithms.HmacSha512Signature
      )
     );

     return Results.Ok(new Dictionary<string, string> {
      { "username", user.Username },
      { "jwt", new JwtSecurityTokenHandler().WriteToken(jwtToken) }
     });
    });

    app.MapControllers();
    app.Run();
  }
}
